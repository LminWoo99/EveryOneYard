package com.example.VideoChatting.controller;

import com.example.VideoChatting.dto.WebSocketMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.service.rtc.RtcChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.servlet.function.ServerResponse.status;

@ExtendWith(MockitoExtension.class)

class RtcChatControllerTest {
    @InjectMocks
    RtcChatController rtcChatController;

    @Mock
    RtcChatService rtcChatService;
    MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rtcChatController).build();
    }
    @Test
    @DisplayName("화상 채팅방 생성 컨트롤러 테스트 ")
    void createRoomTest() throws Exception{

        String roomName = "TestRoom";
        String roomPwd = "123456";
        boolean secretCheck = true;
        int maxUserCnt = 100;

        String secretCheck2 = "true";
        String maxUserCnt2 = "100";

        ChatRoom chatRoom = new ChatRoom();
        rtcChatService.createChatRoom(roomName, roomPwd, secretCheck, maxUserCnt);

        when(rtcChatService.createChatRoom(roomName, roomPwd, true, 100)).thenReturn(chatRoom);

        mockMvc.perform(post("/chat/createRtcRoom")
                        .param("roomName", roomName)
                        .param("roomPwd", roomPwd)
                        .param("secretCheck", secretCheck2)
                        .param("maxUserCnt", maxUserCnt2))
                .andExpect(redirectedUrl("/chat"))
                .andExpect(flash().attributeExists("roomName"));

        verify(rtcChatService, times(2)).createChatRoom(roomName, roomPwd, true, 100);

    }
    @Test
    @DisplayName("webRTC 메서드 테스트")
    void testWebRTC() throws Exception {

        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setData("화상 채팅방 테스트");

        when(rtcChatService.findUserCount(webSocketMessage)).thenReturn(true);

        mockMvc.perform(post("/webrtc/usercount")
                        .flashAttr("webSocketMessage", webSocketMessage))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(rtcChatService, times(1)).findUserCount(webSocketMessage);
    }


}