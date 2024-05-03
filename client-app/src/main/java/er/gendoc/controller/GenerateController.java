package er.gendoc.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import er.gendoc.resentator.DocumentFormat;
import er.gendoc.resentator.ReplaceWordMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class GenerateController {

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    public GenerateController(ClientRegistrationRepository clientRegistrationRepository,
                                  OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                                  OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        this.auth2AuthorizedClientManager = auth2AuthorizedClientManager;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/select-template")
    public String showTemplateSelectionPage(Model model) {

        HttpHeaders headers = new HttpHeaders();
        var token = getToken();
        headers.setBearerAuth(token);

        // Создаем объект запроса с установленными заголовками
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Получаем данные о templates с сервера с использованием объекта запроса
        ResponseEntity<Map<Long, String>> response = restTemplate.exchange(
                "http://localhost:8083/templates/all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<Long, String>>() {});

        // Извлекаем список шаблонов из ResponseEntity
        Map<Long, String> templates = response.getBody();

        // Помещаем список шаблонов в модель, чтобы они были доступны в HTML шаблоне
        model.addAttribute("templates", templates);

        // Возвращаем имя HTML шаблона, который будет использоваться для отображения страницы выбора шаблона
        return "template-selection";
    }


    @GetMapping("/template/{id}")
    public String editTemplate(@PathVariable("id") Long id, Model model) {
        // Создаем HTTP заголовки и устанавливаем токен доступа
        HttpHeaders headers = new HttpHeaders();
        var token = getToken();
        headers.setBearerAuth(token);

        // Создаем объект запроса с установленными заголовками
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Делаем GET запрос на сервер, передавая идентификатор шаблона
        ResponseEntity<List<ReplaceWordMapping>> response = restTemplate.exchange(
                "http://localhost:8083/templates/" + id,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ReplaceWordMapping>>() {}
        );

        // Получаем replace words выбранного шаблона из ответа сервера
        List<ReplaceWordMapping> replaceWords = response.getBody();

        // Помещаем replace words в модель, чтобы они были доступны в HTML шаблоне
        model.addAttribute("replaceWords", replaceWords);
        model.addAttribute("templateId", id);
        // Возвращаем имя HTML шаблона, который будет использоваться для отображения редактирования шаблона
        return "document-creation-form";
    }

//    @PostMapping("/template/{id}/generate")
//    @ResponseBody
//    public ResponseEntity<?> generateDocument(@PathVariable("id") Long id,
//                                                   @RequestBody GenerateDocumentRequest request)
//            throws IOException {
//
//        // Получаем токен доступа
//        String token = getToken();
//
//        // Формируем URL для отправки запроса на сервер
//        String url = "http://localhost:8083/templates/" + id + "/generate";
//
//        // Создаем заголовки для запроса
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(token);
//        // Создаем объект запроса с установленными заголовками
//        HttpEntity<GenerateDocumentRequest> entity = new HttpEntity<>(request, headers);
//
//        // Отправляем POST-запрос на сервер и получаем ответ
//        ResponseEntity<?> responseEntity = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                entity,
//                byte[].class);
//
//        // Возвращаем ответ клиенту
//        return responseEntity;
//    }

    @PostMapping("/template/{id}/generate")
    public ResponseEntity<?> generateDocument(@PathVariable("id") Long id,
                                              @RequestParam("format") DocumentFormat format,
                                              @RequestParam Map<String, String> variables)
            throws IOException {

        // Получаем токен доступа
        String token = getToken();
        GenerateDocumentRequest request= new GenerateDocumentRequest(format,variables);
        // Формируем URL для отправки запроса на сервер
        String url = "http://localhost:8083/templates/" + id + "/generate";

        // Создаем заголовки для запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        // Создаем объект запроса с установленными заголовками
        HttpEntity<GenerateDocumentRequest> entity = new HttpEntity<>(request, headers);

        // Отправляем POST-запрос на сервер и получаем ответ
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                byte[].class);

        // Возвращаем ответ клиенту
        return responseEntity;
    }

    private String getToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("client-app")
                .principal(SecurityContextHolder.getContext().getAuthentication())
                .build();

        return auth2AuthorizedClientManager.authorize(authorizeRequest)
                .getAccessToken().getTokenValue();
    }
    public static class GenerateDocumentRequest {
        //@JsonProperty("format")
        private DocumentFormat format;
        //@JsonProperty("variables")
        private Map<String, String> variables;

        public GenerateDocumentRequest() {
        }

        public GenerateDocumentRequest(DocumentFormat format, Map<String, String> variables) {
            this.format = format;
            this.variables = variables;
        }

        public DocumentFormat getFormat() {
            return format;
        }

        public void setFormat(DocumentFormat format) {
            this.format = format;
        }

        public Map<String, String> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, String> variables) {
            this.variables = variables;
        }
    }
}
