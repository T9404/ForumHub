package com.example.message.db;

import com.example.message.dto.MessageFilter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface FilterMessageRepository {
    List<MessageEntity> findAll(MessageFilter filter, PageRequest pageRequest);

    int count(MessageFilter filter);
}
