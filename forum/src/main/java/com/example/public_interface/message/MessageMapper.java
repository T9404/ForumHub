package com.example.public_interface.message;

import com.example.core.message.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper( MessageMapper.class );

    MessageResponseDto toResponse(MessageEntity messageEntity);
}
