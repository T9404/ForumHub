package com.example.core.attachment.client;

import com.example.core.attachment.client.dto.UploadResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "storageClient")
public interface StorageClient {

    @GetMapping(value = "/api/v1/files/upload", consumes = "multipart/form-data")
    UploadResponseDto uploadFile(@RequestPart("file") MultipartFile file);
}
