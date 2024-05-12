package a.gleb.reactive.controller;

import a.gleb.reactive.service.UploadHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload-history")
public class UploadHistoryRestController {

    private final UploadHistoryService uploadHistoryService;

    @PostMapping
    public Mono<Void> uploadHistory(@RequestPart("file") Mono<FilePart> file) {
        return uploadHistoryService.uploadFile(file);
    }
}
