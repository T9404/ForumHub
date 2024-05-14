package com.example.wishlist.db;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wishlist")
public class WishlistEntity {

    @EmbeddedId
    private WishlistId wishlistId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
