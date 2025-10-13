package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.entity.ChatMessage;
import com.devteria.identity_service.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    @GetMapping("/history")
    public ApiResponse<List<ChatMessage>> findHistory(
            @RequestParam String userID1,
            @RequestParam String userID2){
        return ApiResponse.<List<ChatMessage>>builder()
                .code(1000)
                .data(chatMessageService.getChatMessages(userID1,userID2))
                .build();
    }

}
