package com.example.core.user.outbox.repository;

import com.example.core.user.outbox.repository.entity.OutboxUserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxUserRoleRepository extends JpaRepository<OutboxUserRoleEntity, UUID> {

    @Query(value = """
        UPDATE role_outbox
        SET status = 'PENDING', retry_count = retry_count + 1, updated_at = CURRENT_TIMESTAMP
        WHERE id IN (
            SELECT id FROM role_outbox
            WHERE status = :status
            ORDER BY created_at
            FOR UPDATE SKIP LOCKED
            LIMIT 1000
        )
        RETURNING id, status, user_id, created_at, updated_at, retry_count;
    """, nativeQuery = true)
    List<OutboxUserRoleEntity> setAllPending(@Param("status") String status);

    @Query(value = """
        SELECT id, status, user_id, created_at, updated_at, retry_count FROM role_outbox
        WHERE status = :status
        FOR UPDATE SKIP LOCKED
        LIMIT 1000
    """, nativeQuery = true)
    List<OutboxUserRoleEntity> findAllByStatus(@Param("status") String status);
}
