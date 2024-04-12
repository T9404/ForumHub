package com.example.topic.mapper;

import com.example.topic.db.TopicEntity;
import com.example.topic.controller.response.TopicResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TopicMapper {
    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicResponseDto toResponse(TopicEntity topicId);
}
