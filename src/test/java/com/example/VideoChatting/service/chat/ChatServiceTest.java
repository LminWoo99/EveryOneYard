package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.MessageType;
import com.example.VideoChatting.repository.ChatMessageRepository;
import com.example.VideoChatting.repository.ChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("대화 저장 테스트")
    void saveMessageTest() {
        //given
        ChatDto chatDto = ChatDto.builder()
                .type(MessageType.TALK)
                .roomId("testRoomId")
                .sender("testSender")
                .message("Hello, World!")
                .s3DataUrl("https://example.com/some-file")
                .fileName("example.txt")
                .fileDir("/files")
                .build();

        //when
        chatService.saveMessage(chatDto);

        //then
        verify(chatMessageRepository, times(1)).save(any());
    }
    @Test
    @DisplayName("대화 조회 테스트")
    void loadMessage_ReturnsListOfChatDto() {
        // Given
        String roomId = "room1";
        Date now = new Date();

        ChatMessage message1 = ChatMessage.builder()
                .type(MessageType.TALK)
                .sender("user1")
                .message("Hello")
                .roomId(roomId)
                .createdAt(now)
                .build();

        ChatMessage message2 = ChatMessage.builder()
                .type(MessageType.TALK)
                .sender("user2")
                .message("Hi there")
                .roomId(roomId)
                .createdAt(now)
                .build();

        List<ChatMessage> mockMessages = Arrays.asList(message1, message2);

        when(chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId)).thenReturn(mockMessages);

        // When
        List<ChatDto> result = chatService.loadMessage(roomId);

        // Then
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getSender());
        assertEquals("Hello", result.get(0).getMessage());
        assertEquals("user2", result.get(1).getSender());
        assertEquals("Hi there", result.get(1).getMessage());

        verify(chatMessageRepository).findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Test
    @DisplayName("빈 대화 조회 테스트")
    void loadMessage_EmptyList_ReturnsEmptyList() {
        // Given
        String roomId = "emptyRoom";
        when(chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId)).thenReturn(Arrays.asList());

        // When
        List<ChatDto> result = chatService.loadMessage(roomId);

        // Then
        assertTrue(result.isEmpty());
        verify(chatMessageRepository).findByRoomIdOrderByCreatedAtAsc(roomId);
    }
}