package com.devteria.identity_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    String senderId;
    String senderName;
    String receiverId;
    String receiverName;
    String message;     // nội dung tin nhắn
    String type;        // TEXT / IMAGE / SYSTEM...
    String timestamp;   // ISO 8601 string, vd: 2025-10-10T12:30:45Z
}
