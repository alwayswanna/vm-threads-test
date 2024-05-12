package a.gleb.virtual.service;

import a.gleb.virtual.db.repository.PersonRepository;
import a.gleb.virtual.model.PersonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<PersonResponse> getAllPersons() {
        log.trace("PersonService#getAllPersons(): thread={}", Thread.currentThread());
        return personRepository.findAll().stream()
                .map(PersonResponse::fromEntity)
                .toList();
    }
}
