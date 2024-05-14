package com.example.core.channel.impl.mail;

import com.example.core.channel.NotificationChannel;
import com.example.core.channel.impl.mail.client.UserClient;
import com.example.core.mail.MailService;
import com.example.core.notification.repository.entity.NotificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailNotificationChannel implements NotificationChannel {
    private final MailService mailService;
    private final UserClient userClient;

    @Override
    public void execute(NotificationEntity notification) {
        var userDto = userClient.getUser(notification.getReceiverId());
        mailService.sendMail(userDto.email(), notification.getTitle(), notification.getContent());
    }
}
