package a.gleb.virtual.service;

import a.gleb.virtual.db.entity.PersonEntity;
import a.gleb.virtual.db.entity.UploadHistoryEntity;
import a.gleb.virtual.db.repository.PersonRepository;
import a.gleb.virtual.db.repository.UploadHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static a.gleb.virtual.db.entity.UploadStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadHistoryService {

    private static final String APPLICATION_NAME = "virtual";

    private final PersonRepository personRepository;
    private final UploadHistoryRepository uploadHistoryRepository;

    public void uploadFile(@NonNull MultipartFile file) {
        log.info("UploadHistoryService#uploadFile(): thread={}", Thread.currentThread());
        var currentTime = LocalDateTime.now();

        var historicalEntity = UploadHistoryEntity.builder()
                .applicationName(APPLICATION_NAME)
                .fileName(file.getOriginalFilename())
                .status(PENDING)
                .version(getVersion(currentTime))
                .createdDate(currentTime)
                .build();

        var saveHistoricalEntity = uploadHistoryRepository.save(historicalEntity);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> parseAndUploadFileRows(file, saveHistoricalEntity));
        } catch (Exception e) {
            log.error(
                    "UploadHistoryService#uploadFile(): error while create executor for upload. [message={}]",
                    e.getMessage(),
                    e
            );
            saveHistoricalEntity.setStatus(FAIL);
            uploadHistoryRepository.save(saveHistoricalEntity);
        }
    }

    private void parseAndUploadFileRows(MultipartFile file, UploadHistoryEntity saveHistoricalEntity) {
        log.info("UploadHistoryService#parseAndUploadFileRows(): thread={}", Thread.currentThread());
        try (var inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

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

            personRepository.saveAll(personEntities);
            saveHistoricalEntity.setStatus(SUCCESS);
            uploadHistoryRepository.save(saveHistoricalEntity);
        } catch (Exception e) {
            log.error("UploadHistoryService#parseAndUploadFileRows(): error while upload file. [message={}]", e.getMessage(), e);
            saveHistoricalEntity.setStatus(FAIL);
            uploadHistoryRepository.save(saveHistoricalEntity);
        }
    }

    private Long getVersion(LocalDateTime currentTime) {
        return uploadHistoryRepository.findFirstByCreatedDateBefore(currentTime)
                .map(UploadHistoryEntity::getVersion)
                .map(it -> it + 1L)
                .orElse(1L);
    }
}
