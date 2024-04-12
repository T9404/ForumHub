package com.example.attachment.db;

import com.example.attachment.client.StorageClient;
import com.example.attachment.enums.AttachmentEvent;
import com.example.attachment.mapper.AttachmentMapper;
import com.example.message.db.MessageEntity;
import com.example.exception.BusinessException;
import com.example.attachment.dto.AttachmentDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final StorageClient storageClient;

    public void saveAttachments(MessageEntity message, MultipartFile[] attachments) {
        for (MultipartFile attachment : attachments) {

            var uploadResponseDto = storageClient.uploadFile(attachment);
            var attachmentEntity = AttachmentEntity.builder()
                    .fileId(uploadResponseDto.fileId())
                    .message(message)
                    .build();
            attachmentRepository.save(attachmentEntity);

            log.info("Attachment with name {} has been saved", attachment.getOriginalFilename());
        }
    }

    public AttachmentEntity findById(UUID attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException(AttachmentEvent.ATTACHMENT_NOT_FOUND, "Attachment not found"));
    }

    public Set<AttachmentDto> getAttachments(UUID messageId) {
        return attachmentRepository.findAllByMessageId(messageId).stream()
                .map(AttachmentMapper.INSTANCE::toDto)
                .collect(Collectors.toSet());
    }

    /**
     * The forum does not allow to delete attachments from the storage because of the 'Federal Law on Personal Data'.
     */
    public void deleteAttachment(UUID attachmentId) {
        var attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException(AttachmentEvent.ATTACHMENT_NOT_FOUND, "Attachment with id " + attachmentId + " not found"));

        attachmentRepository.delete(attachment);
    }

    public void deleteAllAttachments(MessageEntity message) {
        attachmentRepository.deleteAllByMessage(message);
    }
}
