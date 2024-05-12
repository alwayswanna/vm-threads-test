package a.gleb.virtual.db.repository;

import a.gleb.virtual.db.entity.UploadHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UploadHistoryRepository extends JpaRepository<UploadHistoryEntity, UUID> {

    Optional<UploadHistoryEntity> findFirstByCreatedDateBefore(LocalDateTime date);
}
