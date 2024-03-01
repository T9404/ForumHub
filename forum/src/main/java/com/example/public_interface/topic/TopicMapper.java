package com.example.public_interface.topic;

import com.example.core.topic.TopicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TopicMapper {
    TopicMapper INSTANCE = Mappers.getMapper( TopicMapper.class );

    TopicResponseDto toResponse(TopicEntity topicId);
    TopicEntity toEntity(TopicResponseDto createTopicRequestDto);
}
