package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :userID1 AND m.receiverId = :userID2) OR " +
            "(m.senderId = :userID2 AND m.receiverId = :userID1) " +
            "ORDER BY m.timestamp DESC")
    List<ChatMessage> findConversation(@Param("userID1") String userID1,@Param("userID2") String userID2);
}
