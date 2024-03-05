package com.example.core.message;

import com.example.core.message.db.MessageEntity;
import com.example.public_interface.message.MessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageResponseDto toResponse(MessageEntity messageEntity);
}
