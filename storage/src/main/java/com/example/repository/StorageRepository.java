package com.example.repository;

import com.example.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StorageRepository extends JpaRepository<FileEntity, UUID> {
}
