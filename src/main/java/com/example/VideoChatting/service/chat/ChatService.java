


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

        // 1. 직렬화
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));

        // 2. redis 저장
        redisTemplate.opsForList().rightPush(chatDto.getRoomId(), chatDto);

        // 3. expire 을 이용해서, Key 를 만료시킬 수 있음
        redisTemplate.expire(chatDto.getRoomId(), 20, TimeUnit.MINUTES);
    }
    // 6. 대화 조회 - Redis & DB
    public List<ChatDto> loadMessage(String roomId) {
        List<ChatDto> messageList = new ArrayList<>();

        // Redis 에서 해당 채팅방의 메시지 100개 가져오기
        List<ChatDto> redisMessageList = redisTemplate.opsForList().range(roomId, 0, 99);

        // 4. Redis 에서 가져온 메시지가 없다면, DB 에서 메시지 100개 가져오기
        if (redisMessageList == null || redisMessageList.isEmpty()) {
            ChatRoom room = chatRoomRepository.findByRoomId(roomId);
            // 5.
            log.info("db에서 가져오기");
            List<ChatMessage> dbMessageList = chatMessageRepository.findTop100ByChatRoomOrderByCreatedAtAsc(room);

            for (ChatMessage chatMessage : dbMessageList) {
                ChatDto chatDto = new ChatDto(chatMessage);
                messageList.add(chatDto);
                redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));      // 직렬화
                redisTemplate.opsForList().rightPush(roomId, chatDto);                                // redis 저장
            }
        } else {
            // 7.
            messageList.addAll(redisMessageList);
        }

        return messageList;
    }

}

