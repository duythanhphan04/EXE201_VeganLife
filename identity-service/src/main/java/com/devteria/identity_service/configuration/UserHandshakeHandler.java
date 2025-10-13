package com.devteria.identity_service.configuration;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery();
        String userId = "anonymous";

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("userID=")) {
                    userId = param.substring("userID=".length());
                    break;
                }
            }
        }

        System.out.println("🔐 WebSocket connected with Principal ID: " + userId);
        final String finalUserId = userId;
        return () -> finalUserId;
    }
}