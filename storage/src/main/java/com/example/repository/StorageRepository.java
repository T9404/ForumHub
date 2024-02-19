package com.example.repository;

import com.example.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StorageRepository extends JpaRepository<FileEntity, UUID> {
}
