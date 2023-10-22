package com.example.VideoChatting.controller;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.service.chat.ChatRoomService;
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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private SimpMessageSendingOperations template;

    @Mock
    private ChatRoomService chatRoomService;

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
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}