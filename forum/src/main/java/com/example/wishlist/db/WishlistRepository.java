package com.example.wishlist.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<WishlistEntity, WishlistId> {
    Page<WishlistEntity> findAllByWishlistIdUserId(UUID userId, PageRequest pageable);

    List<WishlistEntity> findByWishlistIdTopicId(UUID topicId);
}
