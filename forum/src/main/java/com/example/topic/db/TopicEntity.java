package com.example.topic.db;

import com.example.category.db.CategoryEntity;
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
@Table(name = "topic")
@NamedEntityGraph(
        name = "TopicEntity.categoryWithCreator",
        attributeNodes = @NamedAttributeNode(value = "category", subgraph = "categoryWithCreator"),
        subgraphs = @NamedSubgraph(
                name = "categoryWithCreator",
                attributeNodes = @NamedAttributeNode("creatorId")
        )
)
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "topic_id")
    private UUID topicId;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private CategoryEntity category;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "modification_at")
    private OffsetDateTime modificationAt;

    @Column(name = "creator_id")
    private UUID creatorId;

    @Column(name = "status")
    private String status;
}
