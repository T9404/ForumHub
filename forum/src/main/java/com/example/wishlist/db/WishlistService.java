package com.example.wishlist.db;

import com.example.topic.db.TopicService;
import com.example.wishlist.controller.dto.WishlistDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final TopicService topicService;

    @Transactional
    public WishlistDto save(UUID userId, UUID topicId) {
        checkTopicExists(topicId);

        var wishlist = WishlistEntity.builder()
                .wishlistId(new WishlistId(userId, topicId))
                .createdAt(OffsetDateTime.now())
                .build();

        wishlistRepository.save(wishlist);
        return mapToWishlistDto(wishlist);
    }

    @Transactional(readOnly = true)
    public List<WishlistEntity> findByTopicId(UUID topicId) {
        return wishlistRepository.findByWishlistIdTopicId(topicId);
    }

    @Transactional(readOnly = true)
    public Page<WishlistDto> getAll(Pageable pageable, UUID userId) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createdAt").descending());
        return wishlistRepository.findAllByWishlistIdUserId(userId, pageRequest)
                .map(this::mapToWishlistDto);
    }

    @Transactional
    public void delete(UUID userId, UUID topicId) {
        WishlistId id = new WishlistId(userId, topicId);

        wishlistRepository.deleteById(id);
    }

    private void checkTopicExists(UUID topicId) {
        topicService.findById(topicId);
    }

    private WishlistDto mapToWishlistDto(WishlistEntity entity) {
        return new WishlistDto(
                entity.getWishlistId().getUserId(),
                entity.getWishlistId().getTopicId(),
                entity.getCreatedAt()
        );
    }
}
