


package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.repository.ChatMessageRepository;
import com.example.VideoChatting.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;


    // 대화 저장
    public void saveMessage(ChatDto chatDto) {
        // DB 저장
        chatMessageRepository.save(chatDto.toEntity());
    }
    // 대화 조회 - Redis & DB
    public List<ChatDto> loadMessage(String roomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        return chatMessageList.stream().map(ChatMessage::toDto).collect(Collectors.toList());

    }

}
