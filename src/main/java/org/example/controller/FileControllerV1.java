package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.FileDto;
import org.example.service.FileService;
import org.example.service.FileUploadService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FileControllerV1 {


    private final FileService fileService;
    private final UserService userService;
    private final FileUploadService fileUploadService;

    @PostMapping(path = "/users/{userId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<FileDto> uploadFile(@PathVariable Integer userId, @RequestParam("filePart") FilePart filePart) throws IOException {

        userService.findById(userId);

        return fileUploadService.uploadFile(filePart, userId).switchIfEmpty(Mono.error(
                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed")
        ));

    }
}
