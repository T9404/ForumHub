package com.example.category.db;

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
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "previous_category_id")
    private UUID previousCategoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "modification_at")
    private OffsetDateTime modificationAt;

    @Column(name = "creator_id")
    private UUID creatorId;
}
