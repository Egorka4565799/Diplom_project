package er.gendoc.controller;

import er.gendoc.resentator.Category;
import er.gendoc.resentator.Document;
import er.gendoc.resentator.Template;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class ArhiveController {
    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    public ArhiveController(ClientRegistrationRepository clientRegistrationRepository,
                              OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                              OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        this.auth2AuthorizedClientManager = auth2AuthorizedClientManager;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/documents")
    public String showTemplateSelectionPage(Model model) {

        HttpHeaders headers = new HttpHeaders();
        var token = getToken();
        headers.setBearerAuth(token);

        // Создаем объект запроса с установленными заголовками
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Получаем данные о templates с сервера с использованием объекта запроса
        ResponseEntity<List<Document>> responseTemplate = restTemplate.exchange(
                "http://localhost:8083/documents",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Document>>() {});

        // Извлекаем список шаблонов из ResponseEntity
        List<Document> documents = responseTemplate.getBody();


        // Помещаем список шаблонов в модель, чтобы они были доступны в HTML шаблоне
        model.addAttribute("documents", documents);

        // Возвращаем имя HTML шаблона, который будет использоваться для отображения страницы выбора шаблона
        return "archive";
    }

    private String getToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("client-app")
                .principal(SecurityContextHolder.getContext().getAuthentication())
                .build();

        return auth2AuthorizedClientManager.authorize(authorizeRequest)
                .getAccessToken().getTokenValue();
    }
}
