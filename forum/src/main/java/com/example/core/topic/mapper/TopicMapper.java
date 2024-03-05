package com.example.core.topic;

import com.example.public_interface.topic.TopicResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TopicMapper {
    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicResponseDto toResponse(TopicEntity topicId);

    TopicEntity toEntity(TopicResponseDto createTopicRequestDto);
}
