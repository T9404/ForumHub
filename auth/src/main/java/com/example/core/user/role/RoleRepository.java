package com.example.core.user.role;

import com.example.core.user.role.role.RoleEntity;
import com.example.core.user.role.role.RoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, RoleId> {
    @Modifying
    @Query(value = "UPDATE role SET role = ?2 WHERE user_id = ?1", nativeQuery = true)
    void updateRole(@Param("user_id") UUID userId, String role);

    Set<RoleEntity> findAllByIdUserId(UUID userId);

    @Modifying
    @Query(value = "DELETE FROM role WHERE user_id = ?1 AND role = ?2", nativeQuery = true)
    void deleteAllByIdUserIdAndIdRole(UUID userId, String role);
}
