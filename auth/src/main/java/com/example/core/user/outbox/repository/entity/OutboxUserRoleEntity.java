package com.example.core.user.outbox.repository.entity;

import com.example.core.user.outbox.repository.enums.OutboxUserRoleStatus;
import com.example.core.user.repository.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_outbox")
public class OutboxUserRoleEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID outboxId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OutboxUserRoleStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
