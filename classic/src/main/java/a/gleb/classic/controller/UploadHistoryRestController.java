package a.gleb.classic.controller;

import a.gleb.classic.service.UploadHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload-history")
public class UploadHistoryRestController {

    private final UploadHistoryService uploadHistoryService;

    @PostMapping
    public void loadPerson(@RequestParam("file") MultipartFile file) {
        uploadHistoryService.uploadFile(file);
    }
}
