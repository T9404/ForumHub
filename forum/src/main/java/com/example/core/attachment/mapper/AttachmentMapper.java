package com.example.core.attachment.mapper;

import com.example.core.attachment.db.AttachmentEntity;
import com.example.public_interface.attachment.AttachmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttachmentMapper {
    AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

    AttachmentDto toDto(AttachmentEntity attachmentEntity);
}
