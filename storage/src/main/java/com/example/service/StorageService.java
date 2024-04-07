package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.security.dto.DownloadFileDto;
import com.example.security.dto.FileDto;
import com.example.security.dto.UploadResponseDto;
import com.example.entity.FileEntity;
import com.example.enums.event.StorageEvent;
import com.example.exception.BusinessException;
import com.example.mapper.FileMapper;
import com.example.repository.StorageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;
    private final AmazonS3 s3Client;
    private static String bucketName;

    @Value("${application.bucket.name}")
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public DownloadFileDto downloadFile(UUID fileId) {
        S3Object s3Object = s3Client.getObject(bucketName, String.valueOf(fileId));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        var file = storageRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(StorageEvent.FILE_NOT_FOUND, "File not found"));

        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return new DownloadFileDto(new ByteArrayResource(content), file.getName());
        } catch (IOException e) {
            log.error("Error downloading file", e);
            return null;
        }
    }

    public UploadResponseDto uploadFile(MultipartFile file) {
        var fileObj = convertMultiPartFileToFile(file);

        var fileEntity = new FileEntity();
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setSize(file.getSize());
        fileEntity.setTimeCreated(new Timestamp(System.currentTimeMillis()));
        var savedFile = storageRepository.save(fileEntity);

        s3Client.putObject(new PutObjectRequest(bucketName, String.valueOf(savedFile.getFileId()), fileObj));
        fileObj.delete();
        return new UploadResponseDto(savedFile.getFileId());
    }

    public List<FileDto> getAllFiles() {
        return storageRepository.findAll().stream()
                .map(FileMapper::toDto)
                .toList();
    }


    private File convertMultiPartFileToFile(MultipartFile file) {
        var convertedFile = new File(file.getOriginalFilename());

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipart file to file", e);
        }
        return convertedFile;
    }
}
