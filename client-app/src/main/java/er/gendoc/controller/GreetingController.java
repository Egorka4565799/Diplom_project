package er.gendoc.controller;

import er.gendoc.resentator.Greetings;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestClient;

import java.security.Principal;


@Controller
public class GreetingController {

    private final RestClient restClient;

    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    public GreetingController(ClientRegistrationRepository clientRegistrationRepository,
                              OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository){

        this.auth2AuthorizedClientManager= new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,oAuth2AuthorizedClientRepository);

        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8083")
                .requestInterceptor(((request, body, execution) -> {
                    if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                        var token = this.auth2AuthorizedClientManager.authorize(
                                OAuth2AuthorizeRequest.withClientRegistrationId("client-app")
                                        .principal(SecurityContextHolder.getContext().getAuthentication())
                                        .build())
                                .getAccessToken().getTokenValue();

                       request.getHeaders().setBearerAuth(token);
                    }

                    return execution.execute(request, body);
                }))
                .build();
    }

    @ModelAttribute("principal")
    public Principal principal(Principal principal) {
        return principal;
    }

    @GetMapping("/")
    public String getGreetingsPage(Model model) {
        model.addAttribute("greetings",
                this.restClient.get()
                        .uri("/greetings-api/greetings")
                        .retrieve()
                        .body(Greetings.class));
        return "main";
    }

}
