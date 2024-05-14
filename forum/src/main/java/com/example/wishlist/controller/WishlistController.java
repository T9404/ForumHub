package com.example.wishlist.controller;

import com.example.security.dto.user.User;
import com.example.wishlist.controller.dto.WishlistDto;
import com.example.wishlist.db.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/wishlists")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/{topic_id}")
    public WishlistDto save(@PathVariable(name = "topic_id") String topicId,
                            @AuthenticationPrincipal User user) {
        return wishlistService.save(user.getUserId(), UUID.fromString(topicId));
    }

    @DeleteMapping("/{topic_id}")
    public void delete(@PathVariable(name = "topic_id") String topicId,
                       @AuthenticationPrincipal User user) {
        wishlistService.delete(user.getUserId(), UUID.fromString(topicId));
    }

    @GetMapping("/all")
    public Page<WishlistDto> getAll(Pageable pageable,
                                    @AuthenticationPrincipal User user) {
        return wishlistService.getAll(pageable, user.getUserId());
    }
}
