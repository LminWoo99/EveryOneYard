


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
    private final ChatRoomRepository chatRoomRepository;
    private final RedisTemplate<String, ChatDto> redisTemplate;

    // 대화 저장
    public void saveMessage(ChatDto chatDto) {
        ChatRoom room = chatRoomRepository.findByRoomId(chatDto.getRoomId());
        // DB 저장
        ChatMessage chatMessage = new ChatMessage(chatDto.getSender(),
                chatDto.getMessage(), chatDto.getS3DataUrl(), room, chatDto.getRoomId(), chatDto.getFileName(), chatDto.getFileDir());
        chatMessageRepository.save(chatMessage);

        String cacheKey = "chat:" + chatDto.getRoomId();
        redisTemplate.delete(cacheKey);
    }
    // 대화 조회 - Redis & DB
    public List<ChatDto> loadMessage(String roomId) {
        String cacheKey = "chat:" + roomId;
        List<ChatDto> cachedMessages = redisTemplate.opsForList().range(cacheKey, 0, -1);

        if (cachedMessages != null && !cachedMessages.isEmpty()) {
            return cachedMessages;
        }

        log.info("DB에서 가져오기");
        ChatRoom room = chatRoomRepository.findByRoomId(roomId);
        List<ChatMessage> dbMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);
        List<ChatDto> messageList = dbMessages.stream()
                .map(ChatDto::new)
                .collect(Collectors.toList());

        if (!messageList.isEmpty()) {
            redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatDto.class));
            redisTemplate.opsForList().rightPushAll(cacheKey, messageList);
            redisTemplate.expire(cacheKey, 20, TimeUnit.MINUTES);
        }

        return messageList;
    }

}
