package com.example.core.message;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface FilterMessageRepository {
    List<MessageEntity> findAll(MessageFilter filter, PageRequest pageRequest);
}
