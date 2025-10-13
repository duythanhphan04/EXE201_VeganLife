package com.devteria.identity_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Native WebSocket (ưu tiên dùng nếu trình duyệt hỗ trợ)
        registry.addEndpoint("/chat-websocket")
                .setHandshakeHandler(new UserHandshakeHandler())
                .setAllowedOriginPatterns("*");

        // SockJS fallback
        registry.addEndpoint("/chat-websocket")
                .setHandshakeHandler(new UserHandshakeHandler())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
