package com.example.VideoChatting.repository;

import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    Optional<ChatMessage> findById(Long id);

}
