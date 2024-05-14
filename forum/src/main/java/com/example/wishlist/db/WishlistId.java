package com.example.wishlist.db;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class WishlistId implements Serializable {
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "topic_id")
    private UUID topicId;
}
