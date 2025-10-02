package com.devteria.identity_service.controller;

import com.devteria.identity_service.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenAIController {

    private final ChatService chatService;

    public GenAIController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("ask-ai")
    String getResponse(@RequestParam String prompt) {
        return chatService.getResponse(prompt);
    }
}