package org.example.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.springframework.core.io.buffer.DataBufferUtils;


import java.io.IOException;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Service
public class MinioService {

    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucketName;

    public MinioService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public Mono<String> uploadMultipartFile(FilePart filePart) throws IOException {
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

    public ResponseInputStream<GetObjectResponse> getFile(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(request);
    }

    public Mono<Void> deleteFile(String minioPath) {
        return Mono.fromRunnable(() -> {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(minioPath)
                    .build();

            s3Client.deleteObject(request);
        });
    }
}
