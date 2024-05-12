package a.gleb.classic.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "upload_history")
public class UploadHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "application_name")
    private String applicationName;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UploadStatus status;
}
