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

    @GetMapping("/upload_template")
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
        return "upload_template";
    }


    @PostMapping("/template-api-upload-file")
    public ResponseEntity<String> uploadTemplate(@RequestParam("file") MultipartFile file) {
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
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8083/templates/upload-file",
                HttpMethod.POST,
                requestEntity,
                String.class);

        return response;
    }


    @PostMapping("/template-api-upload-replace-word")
    public ResponseEntity<String> uploadTemplate(@RequestBody List<ReplaceWordMapping> replaceWord,
                                                 @RequestParam("fileName") String fileName,
                                                 @RequestParam("categoryId") long categoryId) {
        // Получаем токен доступа
        String token = getToken();

        // Подготавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        System.out.println(
                replaceWord
        );

        System.out.println(
                "Категория: " + categoryId
        );

        // Строим URL с именем файла как параметром
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8083/templates/upload-file-replace-word")
                .queryParam("fileName", fileName)
                .queryParam("categoryId", categoryId);



        // Подготавливаем тело запроса
        HttpEntity<List<ReplaceWordMapping>> requestEntity = new HttpEntity<>(replaceWord, headers);

        // Отправляем запрос на сервер
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(), // Используем построенный URL
                HttpMethod.POST,
                requestEntity,
                String.class);

        return response;
    }

    @PostMapping("/view-replace-word")
    public ResponseEntity<List<String>> viewReplaceWord(@RequestParam("file") MultipartFile file, Model model){
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
        ResponseEntity<List<String>> response = restTemplate.exchange(
                "http://localhost:8083/templates/get-replace-words",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<String>>() {});

        return response;
        // Получаем replace words выбранного шаблона из ответа сервера
        //List<String> replaceWords = response.getBody();

        // Помещаем replace words в модель, чтобы они были доступны в HTML шаблоне
        //model.addAttribute("words", replaceWords);
        //model.addAttribute("file", file);

        //return "upload_template";
    }

    @PostMapping("/add-category")
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8083/categories/add-category")
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
