package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.dto.FileDto;
import org.example.dto.UserDto;
import org.example.mapper.FileMapper;
import org.example.model.FileEntity;
import org.example.model.Status;
import org.example.repository.FileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    public Mono<FileDto> findById(Integer fileId) {

        return Mono.fromCallable(() -> fileRepository.findById(fileId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(fileMapper::entityToFile)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<FileDto> save(String fileName, String minioPath) {
        FileEntity fileEntity = fileMapper.fileToEntity(new FileDto(fileName, minioPath, Status.ACTIVE));

        return Mono.fromCallable(() -> fileRepository.save(fileEntity))
                .map(fileMapper::entityToFile)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteById(Integer fileId) {

        return Mono.fromRunnable(() -> fileRepository.deleteById(fileId))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
