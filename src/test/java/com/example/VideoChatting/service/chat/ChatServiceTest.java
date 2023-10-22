package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.ChatRoom;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private RedisTemplate<String, ChatDto> redisTemplate;

    @Mock
    private ListOperations<String, ChatDto> listOperations;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chatService, "redisTemplate", redisTemplate);
    }

    @Test
    @DisplayName("대화 저장 테스트")
    void saveMessageTest() {
        //given
        ChatDto chatDto = ChatDto.builder()
                .type(ChatDto.MessageType.TALK)
                .roomId("testRoomId")
                .sender("testSender")
                .message("Hello, World!")
                .createdAt(new Date())
                .s3DataUrl("https://example.com/some-file")
                .fileName("example.txt")
                .fileDir("/files")
                .build();

        ChatRoom mockChatRoom = new ChatRoom();
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        when(chatRoomRepository.findByRoomId(anyString())).thenReturn(mockChatRoom);
        System.out.println("chatDto = " + chatDto.getRoomId());

        //when
        chatService.saveMessage(chatDto);

        //then
        verify(chatMessageRepository, times(1)).save(any());
        verify(redisTemplate, times(1)).setValueSerializer(any(Jackson2JsonRedisSerializer.class));
        verify(redisTemplate, times(1)).expire(anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("대화 조회 테스트 - Redis에 메시지가 없을 때")
    void loadMessageTest_NoRedisMessages() {
        // given
        String roomId = "testRoomId";

        List<ChatDto> redisMessageList = new ArrayList<>();

        ChatRoom mockChatRoom = new ChatRoom();
        when(chatRoomRepository.findByRoomId(anyString())).thenReturn(mockChatRoom);

        List<ChatMessage> mockDbMessageList = new ArrayList<>();
        when(chatMessageRepository.findTop100ByChatRoomOrderByCreatedAtAsc(any())).thenReturn(mockDbMessageList);

        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(roomId, 0, 99)).thenReturn(redisMessageList);  // redisTemplate.opsForList().range() 호출 시 목 객체 반환 설정

        //when
        List<ChatDto> result = chatService.loadMessage(roomId);

        // then
        assertEquals(0, result.size());  // 결과 리스트의 크기가 0이어야 함
    }

    @Test
    @DisplayName("대화 조회 테스트 - Redis에 메시지가 있을 때")
    void loadMessageTest_RedisMessages() {
        // given
        String roomId = "testRoomId";


        List<ChatDto> redisMessageList = new ArrayList<>();
        ChatDto chatDto1 = new ChatDto();
        chatDto1.setMessage("Hello");
        ChatDto chatDto2 = new ChatDto();
        chatDto2.setMessage("World");
        redisMessageList.add(chatDto1);
        redisMessageList.add(chatDto2);


        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(roomId, 0, 99)).thenReturn(redisMessageList);

        // when
        List<ChatDto> result = chatService.loadMessage(roomId);

        // then
        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0).getMessage());
        assertEquals("World", result.get(1).getMessage());

    }
}
