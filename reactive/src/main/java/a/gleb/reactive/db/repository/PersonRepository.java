package a.gleb.reactive.db.repository;

import a.gleb.reactive.db.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<PersonEntity, UUID> {
}
