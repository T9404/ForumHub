package com.example.mapper;

import com.example.security.dto.FileDto;
import com.example.entity.FileEntity;

public final class FileMapper {

    public static FileDto toDto(FileEntity fileEntity) {
        return new FileDto(
                fileEntity.getFileId(),
                fileEntity.getName(),
                fileEntity.getSize(),
                fileEntity.getTimeCreated()
        );
    }
}
