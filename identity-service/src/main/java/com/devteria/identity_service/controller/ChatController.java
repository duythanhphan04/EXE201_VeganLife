package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendPrivate")
    public void sendPrivate(ChatMessage message, Principal principal) {
        String senderId = principal.getName(); // ✅ lấy ID từ Handshake
        System.out.printf("📨 %s → %s: %s%n", senderId, message.getReceiverId(), message.getMessage());

        // ✅ Gửi cho người nhận
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                message
        );

        // ✅ Gửi lại cho chính người gửi để hiển thị message của mình
        messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/messages",
                message
        );
    }
}