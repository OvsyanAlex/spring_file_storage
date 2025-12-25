package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.FileDto;
import org.example.model.Status;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileService fileService;
    private final MinioService minioService;
    private final EventService eventService;

    @Transactional
    public Mono<FileDto> uploadFile(FilePart filePart, Integer userId) {

        return minioService.uploadMultipartFile(filePart)
                .flatMap(minioPath -> fileService.save(filePart.filename(), minioPath)
                        .flatMap(fileDto -> eventService.save(userId, fileDto.getId(), Status.CREATED).thenReturn(fileDto))
                        .onErrorResume(e -> minioService.deleteFile(minioPath).then(Mono.error(e))));
    }

    public Flux<DataBuffer> downloadFile(Integer fileId) {

        return fileService.findById(fileId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found")))
                .flatMapMany(fileEntity -> minioService.getFile(fileEntity.getLocation()));
    }

    public Mono<Void> deleteFile(Integer fileId) {

        return fileService.findById(fileId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found")))
                .flatMap(fileEntity -> minioService.deleteFile(fileEntity.getLocation()).then(fileService.deleteById(fileId))
                );
    }
}
