package a.gleb.reactive.db.repository;

import a.gleb.reactive.db.entity.UploadHistoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface UploadHistoryRepository extends ReactiveCrudRepository<UploadHistoryEntity, UUID> {

    Mono<UploadHistoryEntity> findFirstByCreatedDateBefore(LocalDateTime localDateTime);
}
