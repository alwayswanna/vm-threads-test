package a.gleb.reactive.controller;

import a.gleb.reactive.model.PersonResponse;
import a.gleb.reactive.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/person")
public class PersonRestController {

    private final PersonService personService;

    @GetMapping
    public Flux<PersonResponse> getAllPersons() {
        return personService.getAllPersons();
    }
}
