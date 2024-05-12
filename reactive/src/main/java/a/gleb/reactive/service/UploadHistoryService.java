package a.gleb.reactive.service;

import a.gleb.reactive.db.entity.PersonEntity;
import a.gleb.reactive.db.entity.UploadHistoryEntity;
import a.gleb.reactive.db.repository.PersonRepository;
import a.gleb.reactive.db.repository.UploadHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static a.gleb.reactive.db.entity.UploadStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadHistoryService {

    private static final String APPLICATION_NAME = "reactive";

    private final UploadHistoryRepository uploadHistoryRepository;
    private final PersonRepository personRepository;

    public Mono<Void> uploadFile(Mono<FilePart> file) {
        log.info("UploadHistoryService#uploadFile(): thread={}", Thread.currentThread().getName());
        var currentTime = LocalDateTime.now();

        return file.flatMap(it -> buildNewHistoricalEntity(it, currentTime))
                .flatMap(uploadHistoryRepository::save)
                .flatMap(it -> parseAndUploadFileRows(it, file))
                .then();
    }

    private Mono<UploadHistoryEntity> buildNewHistoricalEntity(FilePart it, LocalDateTime currentTime) {
        log.info("UploadHistoryService#buildNewHistoricalEntity(): thread={}", Thread.currentThread().getName());
        return uploadHistoryRepository.findFirstByCreatedDateBefore(currentTime)
                .switchIfEmpty(Mono.just(UploadHistoryEntity.builder().version(0L).build()))
                .map(entity -> UploadHistoryEntity.builder()
                        .status(PENDING)
                        .createdDate(currentTime)
                        .applicationName(APPLICATION_NAME)
                        .fileName(it.filename())
                        .version(entity.getVersion() + 1)
                        .build());
    }

    private Mono<Void> parseAndUploadFileRows(UploadHistoryEntity entity, Mono<FilePart> file) {
        log.info("UploadHistoryService#parseAndUploadFileRows(): thread={}", Thread.currentThread().getName());
        file.map(Part::content)
                .map(it -> {
                    it.flatMap(buffer -> {
                                var inputStream = buffer.asInputStream();
                                return parseAndUploadFileRowsInner(inputStream, entity);
                            })
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                    return Mono.empty();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

        return Mono.empty();
    }

    private Mono<Void> parseAndUploadFileRowsInner(InputStream inputStream, UploadHistoryEntity entity) {
        log.info("UploadHistoryService#parseAndUploadFileRowsInner(): thread={}", Thread.currentThread());
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            var firstSheetInFile = workbook.getSheetAt(0);
            firstSheetInFile.removeRow(firstSheetInFile.getRow(0));
            var rowIterator = firstSheetInFile.rowIterator();
            List<PersonEntity> personEntities = new ArrayList<>();

            while (rowIterator.hasNext()) {
                var currentRow = rowIterator.next();
                personEntities.add(
                        PersonEntity.builder()
                                .firstName(currentRow.getCell(0).getStringCellValue())
                                .lastName(currentRow.getCell(1).getStringCellValue())
                                .login(currentRow.getCell(2).getStringCellValue())
                                .phone(currentRow.getCell(3).getStringCellValue())
                                .build()
                );
            }

            return personRepository.saveAll(personEntities)
                    .flatMap(it -> {
                                entity.setStatus(SUCCESS);
                                return uploadHistoryRepository.save(entity).then();
                            }
                    ).then();

        } catch (Exception e) {
            log.error("UploadHistoryService#parseAndUploadFileRows(): error while upload file. [message={}]", e.getMessage(), e);
            entity.setStatus(FAIL);
            return uploadHistoryRepository.save(entity).then();
        }
    }
}