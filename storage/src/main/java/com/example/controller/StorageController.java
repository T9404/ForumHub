package com.example.controller;

import com.example.dto.FileDto;
import com.example.dto.UploadResponseDto;
import com.example.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
public class StorageController {
    private final StorageService storageService;


    @GetMapping("/download/{file_id}")
    public ResponseEntity downloadFile(@PathVariable("file_id") UUID fileId) {
        var downloadFileDto = storageService.downloadFile(fileId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment;filename=" + downloadFileDto.fileName())
                .body(downloadFileDto.file());
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDto> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        return ResponseEntity.ok().body(storageService.uploadFile(file));
    }

    @GetMapping()
    public ResponseEntity<List<FileDto>> getAllFiles() {
        return ResponseEntity.ok().body(storageService.getAllFiles());
    }
}
