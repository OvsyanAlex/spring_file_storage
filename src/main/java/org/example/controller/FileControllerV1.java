package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.FileDto;
import org.example.service.FileService;
import org.example.service.FileUploadService;
import org.example.service.UserService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FileControllerV1 {


    private final FileService fileService;
    private final UserService userService;
    private final FileUploadService fileUploadService;

    @PreAuthorize("hasAuthority('FILE_UPLOAD_ALL') or " +
                  "(hasAuthority('FILE_UPLOAD_SELF') and #userId == authentication.principal.id)")
    @PostMapping(path = "/users/{userId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<FileDto> uploadFile(@PathVariable Integer userId, @RequestPart("file") FilePart filePart) {
        return userService.findById(userId)
                .flatMap(user -> fileUploadService.uploadFile(filePart, userId))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed")));
    }

    @PreAuthorize("hasAuthority('FILE_READ_ALL') or " +
                  "(hasAuthority('FILE_READ_SELF') and #userId == authentication.principal.id)")
    @GetMapping(path = "/files/{fileId}")
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadFile(@PathVariable Integer fileId) {
        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileUploadService.downloadFile(fileId))
        );
    }

    @PreAuthorize("hasAuthority('FILE_DELETE_ALL')")
    @DeleteMapping(path = "/files/{fileId}")
    public Mono<Void> deleteFile(@PathVariable Integer fileId){
        return fileUploadService.deleteFile(fileId);
    }
}


