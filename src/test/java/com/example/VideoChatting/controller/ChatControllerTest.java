package com.example.VideoChatting.controller;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.MessageType;
import com.example.VideoChatting.service.chat.ChatRoomService;
import com.example.VideoChatting.service.chat.ChatService;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.VideoChatting.service.chat.RedisPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private SimpMessageSendingOperations template;

    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private ChatService chatService;
    @Mock
    private RedisPublisher redisPublisher;
    @InjectMocks
    private ChatController chatController;
    MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }
    @Test
    @DisplayName("채팅방 입장 테스트")
    void enterUser() throws Exception{
        //given
        ChatDto chat = ChatDto.builder()
                .roomId("roomId")
                .sender("sender")
                .build();

        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        //when
        chatController.enterUser(chat, headerAccessor);

        //then
        verify(chatRoomService).addUser(chat.getRoomId(), chat.getSender());
        verify(headerAccessor, times(2)).getSessionAttributes();
        verify(template).convertAndSend(anyString(), any(ChatDto.class));
    }
    @Test
    @DisplayName("채팅 메세지 보내기 테스트")
    void sendMessageTest() throws Exception{
        //given
        ChatDto chatDto = ChatDto.builder()
                .type(MessageType.TALK)
                .roomId("testRoomId")
                .sender("testSender")
                .message("Hello, World!")
                .createdAt(new Date())
                .s3DataUrl("https://example.com/some-file")
                .fileName("example.txt")
                .fileDir("/files")
                .build();
        ChatRoom chatRoom = new ChatRoom();
        doNothing().when(redisPublisher).publish(any(), any());
        //when
        chatController.sendMessage(chatDto);

        //then
        verify(chatService, times(1)).saveMessage(chatDto);
    }
    @Test
    @DisplayName("이전 채팅 메세지 조회")
    void loadMessageTest() throws Exception {
        //given
        String roomId = "testRoomId";
        List<ChatDto> expectedMessages = new ArrayList<>();
        ChatDto chatDto1 = new ChatDto();
        ChatDto chatDto2 = new ChatDto();

        chatDto1.setSender("sender1");
        chatDto2.setSender("sender2");

        chatDto1.setMessage("메세지 테스트 1");
        chatDto2.setMessage("메세지 테스트 2");

        expectedMessages.add(chatDto1);
        expectedMessages.add(chatDto2);

        when(chatService.loadMessage(roomId)).thenReturn(expectedMessages);
        //when
        mockMvc.perform(get("/chat/room/" + roomId + "/message"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[" +
                        "{\"type\":null,\"roomId\":null,\"sender\":\"sender1\",\"message\":\"메세지 테스트 1\",\"createdAt\":null,\"s3DataUrl\":null,\"fileName\":null,\"fileDir\":null}," +
                        "{\"type\":null,\"roomId\":null,\"sender\":\"sender2\",\"message\":\"메세지 테스트 2\",\"createdAt\":null,\"s3DataUrl\":null,\"fileName\":null,\"fileDir\":null}" +
                        "]"));
        //then
        verify(chatService, times(1)).loadMessage(roomId);
    }

    @Test
    @DisplayName("채팅방 유저리스트 테스트")
    void userList() throws Exception {
        // 샘플 데이터 설정
        when(chatRoomService.getUserList("room1"))
                .thenReturn(new ArrayList<>(Arrays.asList("user1", "user2")));

        mockMvc.perform(get("/chat/userlist")
                        .param("roomId", "room1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"user1\", \"user2\"]"));
    }

    @Test
    @DisplayName("채팅에 참여한 유저 닉네임 중복 확인 테스트")
    void isDuplicateName() throws Exception {
        //given
        String username = "lee";
        String roomId = "방1";
        when(chatRoomService.isDuplicateName(roomId, username)).thenReturn(username);
        //when
        ResultActions perform = mockMvc.perform(get("/chat/duplicateName")
                .param("roomId", roomId)
                .param("username", username));
        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string("lee"));

    }
    @Test
    @DisplayName("coturn 서버 설정 값 반환 테스트")
    void turnConfigTest() throws Exception{
        //when
        ResultActions perform = mockMvc.perform(post("/turnconfig"));
        //then
        mockMvc.perform(post("/turnconfig"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}