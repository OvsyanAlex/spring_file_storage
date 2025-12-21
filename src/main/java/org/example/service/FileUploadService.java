package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.FileDto;
import org.example.model.Status;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileService fileService;
    private final MinioService minioService;
    private final EventService eventService;

    @Transactional
    public Mono<FileDto> uploadFile(FilePart filePart, Integer userId) throws IOException {
        return minioService.uploadMultipartFile(filePart)
                .flatMap(minioPath ->
                        fileService.save(filePart.filename(), minioPath)
                                .flatMap(fileDto ->
                                        eventService.save(userId, fileDto.getId(), Status.CREATED)
                                                .thenReturn(fileDto)
                                )
                                .onErrorResume(e ->
                                        minioService.deleteFile(minioPath)
                                                .then(Mono.error(e))
                                )
                );
    }
}
