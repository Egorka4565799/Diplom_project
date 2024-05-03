package sever.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sever.application.presentator.Greetings;

import java.security.Principal;
@RestController
@RequestMapping("/greetings-api/greetings")
public class GreetingsRestController {

    @GetMapping
    public Greetings getGreetings(Principal principal) {
        return new Greetings("Будь как дома, %s!"
                .formatted(principal != null ? principal.getName() : "Путник"));
    }
}