package com.example.core.confirmation.repository;

import com.example.core.confirmation.repository.entity.ConfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, UUID> {
    Optional<ConfirmationTokenEntity> findByToken(String token);
}
