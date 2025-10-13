package com.devteria.identity_service.service;

import com.devteria.identity_service.entity.ChatMessage;
import com.devteria.identity_service.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    @Autowired
    ChatMessageRepository chatMessageRepository;
    public ChatMessage saveChatMessage(String senderID, String receiverID, String content) {
        ChatMessage message = ChatMessage.builder()
                .senderId(senderID)
                .receiverId(receiverID)
                .message(content)
                .timestamp(Instant.now())
                .build();
        return chatMessageRepository.save(message);
    }
    public List<ChatMessage> getChatMessages(String userID1, String userID2) {
        return chatMessageRepository.findConversation(userID1, userID2);
    }

}
