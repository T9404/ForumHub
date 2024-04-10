package com.example.core.message.mapper;

import com.example.core.message.db.MessageEntity;
import com.example.public_interface.attachment.AttachmentDto;
import com.example.rest.message.response.MessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageResponseDto toResponse(MessageEntity messageEntity, Set<AttachmentDto> attachments);

    MessageResponseDto toResponse(MessageEntity messageEntity);
}
