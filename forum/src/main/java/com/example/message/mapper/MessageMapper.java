package com.example.message.mapper;

import com.example.message.db.MessageEntity;
import com.example.attachment.dto.AttachmentDto;
import com.example.message.controller.response.MessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageResponseDto toResponse(MessageEntity messageEntity, Set<AttachmentDto> attachments);

    MessageResponseDto toResponse(MessageEntity messageEntity);
}
