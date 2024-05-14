package com.example.core.user.outbox;

import com.example.core.user.outbox.repository.OutboxUserRoleRepository;
import com.example.core.user.outbox.repository.entity.OutboxUserRoleEntity;
import com.example.core.user.outbox.repository.enums.OutboxUserRoleStatus;
import com.example.core.user.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OutboxUserRoleService {
    private final OutboxUserRoleRepository outboxUserRoleRepository;

    public void save(UserEntity user) {
        var outboxUserRole = new OutboxUserRoleEntity();
        outboxUserRole.setUser(user);
        outboxUserRole.setStatus(OutboxUserRoleStatus.NEW);
        outboxUserRole.setCreatedAt(OffsetDateTime.now());
        outboxUserRole.setUpdatedAt(OffsetDateTime.now());
        outboxUserRoleRepository.save(outboxUserRole);
    }

    public List<OutboxUserRoleEntity> findAllByStatus(OutboxUserRoleStatus status) {
        return outboxUserRoleRepository.findAllByStatus(status.name());
    }

    public List<OutboxUserRoleEntity> setAllPending(OutboxUserRoleStatus status) {
        return outboxUserRoleRepository.setAllPending(status.name());
    }

    public void updateStatus(OutboxUserRoleEntity outboxUserRoleEntity, OutboxUserRoleStatus status) {
        outboxUserRoleEntity.setStatus(status);
        outboxUserRoleRepository.save(outboxUserRoleEntity);
    }
}
