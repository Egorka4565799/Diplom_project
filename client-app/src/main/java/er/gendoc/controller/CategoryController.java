package er.gendoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import er.gendoc.resentator.Category;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
public class CategoryController {

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    public CategoryController(ClientRegistrationRepository clientRegistrationRepository,
                                  OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                                  OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        this.auth2AuthorizedClientManager = auth2AuthorizedClientManager;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/categories")// Открыть страницу загрузки шаблона
    public String getCategoriesEdit(Model model) throws JsonProcessingException {

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
        // Преобразуем список категорий в JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String categoriesJson = objectMapper.writeValueAsString(response.getBody());

        // Добавляем JSON категорий в модель
        model.addAttribute("categoriesJson", categoriesJson);
        model.addAttribute("categories",response.getBody());
        return "categories_view";
    }

    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@RequestParam("categoryName") String categoryName) throws JsonProcessingException {

        // Получаем токен доступа
        String token = getToken();

        System.out.println("Новая категория: " + categoryName);

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
        ResponseEntity<Category> response = restTemplate.exchange(
                builder.toUriString(), // Используем построенный URL
                HttpMethod.POST,
                requestEntity,
                Category.class);

        System.out.println("Тестовый вывод: " + response.getBody());
        return response;
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id,
                                            @RequestParam("categoryName") String categoryName
    ) throws JsonProcessingException {

        // Получаем токен доступа
        String token = getToken();

        System.out.println("Обновленная категория категория: " + categoryName);

        // Подготавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Подготовка URL с параметрами
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8083/categories/"+id)
                .queryParam("categoryName", categoryName);

        // Подготавливаем тело запроса
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Отправляем запрос на сервер
        ResponseEntity<Category> response = restTemplate.exchange(
                builder.toUriString(), // Используем построенный URL
                HttpMethod.PUT,
                requestEntity,
                Category.class);

        System.out.println("Тестовый вывод: " + response.getBody());
        return response;
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) throws JsonProcessingException {

        // Получаем токен доступа
        String token = getToken();

        System.out.println("Удаление категории: " + id);

        // Подготавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Подготовка URL с параметрами
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8083/categories/"+id);

        // Подготавливаем тело запроса
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Отправляем запрос на сервер
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(), // Используем построенный URL
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        return response;
    }
    private String getToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("client-app")
                .principal(SecurityContextHolder.getContext().getAuthentication())
                .build();

        return auth2AuthorizedClientManager.authorize(authorizeRequest)
                .getAccessToken().getTokenValue();
    }
}
