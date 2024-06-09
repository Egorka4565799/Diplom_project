package er.gendoc.controller;


import er.gendoc.resentator.Category;
import er.gendoc.resentator.ReplaceWordMapping;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TemplateRestController {

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    public TemplateRestController(ClientRegistrationRepository clientRegistrationRepository,
                                  OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                                  OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        this.auth2AuthorizedClientManager = auth2AuthorizedClientManager;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/upload_template")// Открыть страницу загрузки шаблона
    public String getGreetingsPage(Model model) {

        // Создаем HTTP заголовки и устанавливаем токен доступа
        HttpHeaders headers = new HttpHeaders();
        var token = getToken();
        headers.setBearerAuth(token);

        // Создаем объект запроса с установленными заголовками
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Делаем GET запрос на сервер, передавая идентификатор шаблона
        ResponseEntity<List<Category>> response = restTemplate.exchange(
                "http://localhost:8083//categories",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Category>>() {}
        );

        model.addAttribute("categories",response.getBody());
        return "upload_template_new";
    }


    @PostMapping("/template-upload")// Загрузка шаблона
    public ResponseEntity<?> uploadTemplate(@RequestParam("file") MultipartFile file) {
        // Получаем токен доступа
        String token = getToken();

        // Подготавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        // Подготавливаем тело запроса
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        // Создаем запрос с заголовками и телом
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Отправляем запрос на сервер во второй контроллер
        ResponseEntity<Long> response = restTemplate.exchange(
                "http://localhost:8083/templates",
                HttpMethod.POST,
                requestEntity,
                Long.class);
        Long fileId = response.getBody();
        System.out.println("id="+fileId);

        // Возвращаем id в JSON формате
        Map<String, Long> responseBody = new HashMap<>();
        responseBody.put("id", fileId);
        return ResponseEntity.ok(responseBody);
    }


    @PostMapping("/template-replace-word")// Внести константные замены для переменных
    public ResponseEntity<String> uploadTemplate(@RequestBody List<ReplaceWordMapping> replaceWord,
                                                 @RequestParam("fileId") Long fileId) {
        // Получаем токен доступа
        String token = getToken();

        // Подготавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        System.out.println(
                replaceWord
        );

        // Строим URL с именем файла как параметром
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8083/templates/"+fileId+"/replace-word");


        // Подготавливаем тело запроса
        HttpEntity<List<ReplaceWordMapping>> requestEntity = new HttpEntity<>(replaceWord, headers);

        // Отправляем запрос на сервер
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(), // Используем построенный URL
                HttpMethod.PUT,
                requestEntity,
                String.class);

        return response;
    }

    @GetMapping("/view-replace-word")// Получить список переменных
    public ResponseEntity<List<String>> viewReplaceWord(@RequestParam("fileId") Long fileId, Model model){
        // Получаем токен доступа
        String token = getToken();

        // Подготавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        // Подготавливаем тело запроса
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Создаем запрос с заголовками и телом
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Отправляем запрос на сервер во второй контроллер
        ResponseEntity<List<String>> response = restTemplate.exchange(
                "http://localhost:8083/templates/"+fileId+"/replace-words",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<String>>() {});

        System.out.println(response);
        return response;
    }

    @PostMapping("/add-category") // Добавить категорию
    public ResponseEntity<String> addCategory(@RequestParam("categoryName") String categoryName){
        // Получаем токен доступа
        String token = getToken();

        System.out.println(
                "Категория: " + categoryName
        );

        // Подготавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Подготовка URL с параметрами
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8083/categories")
                .queryParam("categoryName", categoryName);

        // Подготавливаем тело запроса
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Отправляем запрос на сервер
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(), // Используем построенный URL
                HttpMethod.POST,
                requestEntity,
                String.class);

        return response;
    }



    // Метод для получения токена доступа
    private String getToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("client-app")
                .principal(SecurityContextHolder.getContext().getAuthentication())
                .build();

        return auth2AuthorizedClientManager.authorize(authorizeRequest)
                .getAccessToken().getTokenValue();
    }
}
