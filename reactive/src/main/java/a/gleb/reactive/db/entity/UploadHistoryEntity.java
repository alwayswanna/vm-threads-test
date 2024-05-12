package a.gleb.reactive.db.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "upload_history")
public class UploadHistoryEntity {

    @Id
    private UUID id;

    @Column("version")
    private Long version;

    @Column("application_name")
    private String applicationName;

    @Column("filename")
    private String fileName;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("status")
    private UploadStatus status;
}
