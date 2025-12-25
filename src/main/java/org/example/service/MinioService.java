package org.example.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;


@Service
public class MinioService {

    private final S3Client s3Client;
    private final DataBufferFactory dataBufferFactory;

    @Value("${minio.bucket}")
    private String bucketName;

    public MinioService(S3Client s3Client, DataBufferFactory dataBufferFactory) {
        this.s3Client = s3Client;
        this.dataBufferFactory = dataBufferFactory;
    }

    public Mono<String> uploadMultipartFile(FilePart filePart)  {
        String path = UUID.randomUUID() + "-" + filePart.filename();
        return DataBufferUtils.join(filePart.content()) // Mono<DataBuffer>
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer); // освобождаем DataBuffer

                    PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(path)
                            .contentType(filePart.headers().getContentType() != null
                                    ? filePart.headers().getContentType().toString()
                                    : "application/octet-stream")
                            .build();

                    // блокирующий S3 SDK
                    return Mono.fromCallable(() -> {
                        s3Client.putObject(request, RequestBody.fromBytes(bytes));
                        return path;
                    }).subscribeOn(Schedulers.boundedElastic());
                });
    }

    public Flux<DataBuffer> getFile(String key) {
        return Flux.using(() -> {
                    GetObjectRequest request = GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();
                    return s3Client.getObject(request);
                },
                inputStream ->
                        DataBufferUtils.readInputStream(
                                () -> inputStream,
                                dataBufferFactory,
                                16 * 1024
                        ),
                inputStream -> {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {}
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteFile(String minioPath) {
        return Mono.fromRunnable(() -> {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(minioPath)
                    .build();

            s3Client.deleteObject(request);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
