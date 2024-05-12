package a.gleb.reactive.service;

import a.gleb.reactive.db.repository.PersonRepository;
import a.gleb.reactive.model.PersonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public Flux<PersonResponse> getAllPersons() {
        log.trace("PersonService#getAllPersons(): thread={}", Thread.currentThread());
        return personRepository.findAll()
                .map(PersonResponse::fromEntity);
    }
}
