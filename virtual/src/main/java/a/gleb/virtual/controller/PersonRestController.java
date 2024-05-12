package a.gleb.virtual.controller;

import a.gleb.virtual.model.PersonResponse;
import a.gleb.virtual.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/person")
public class PersonRestController {

    private final PersonService personService;

    @GetMapping
    public List<PersonResponse> getAllPersons() {
        return personService.getAllPersons();
    }
}
