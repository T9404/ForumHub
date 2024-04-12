package com.example.attachment.mapper;

import com.example.attachment.db.AttachmentEntity;
import com.example.attachment.dto.AttachmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttachmentMapper {
    AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

    AttachmentDto toDto(AttachmentEntity attachmentEntity);
}
