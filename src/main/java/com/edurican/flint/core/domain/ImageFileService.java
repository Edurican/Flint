package com.edurican.flint.core.domain;

import io.minio.*;
import io.minio.errors.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageFileService {

    @Value("${minio.bucket.name}")
    private String BUCKET_NAME;

    @Value("${minio.dir.image}")
    private String DIR_IMAGE;

    private final MinioClient minioClient;

    public String saveImageFile(@Valid MultipartFile multipartFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean isExist =
                minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(BUCKET_NAME)
                                .build()
                );

        if(!isExist) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(BUCKET_NAME)
                            .build()
            );
        }

        String fileName = multipartFile.getOriginalFilename();
        String uniqueFileName = DIR_IMAGE + "/" + UUID.randomUUID().toString() + "-" + fileName;


        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(BUCKET_NAME)
                        .object(uniqueFileName)
                        .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                        .contentType(multipartFile.getContentType())
                        .build()
        );

        return uniqueFileName;
    }

    public void deleteImageFile(@Valid String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(isExistImage(filename)) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(filename)
                            .build()
            );
        }
    }

    public boolean isExistImage(String filename) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(filename)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
