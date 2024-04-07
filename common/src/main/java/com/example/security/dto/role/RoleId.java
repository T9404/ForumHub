package com.example.security.dto.role;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RoleId implements Serializable {
    private UUID userId;
    private String role;
}
