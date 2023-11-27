package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.dto.KurentoRoomDto;
import com.example.VideoChatting.dto.WebSocketMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.repository.ChatMessageRepository;
import com.example.VideoChatting.repository.ChatRoomRepository;
import com.example.VideoChatting.service.rtc.RtcChatService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kurento.client.KurentoClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;


import static com.example.VideoChatting.entity.ChatType.RTC;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RtcChatServiceTest {

    @InjectMocks
    private RtcChatService rtcChatService;

    @Mock
    private KurentoClient kurentoClient;

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private WebSocketSession webSocketSession;

    @Mock
    private RedisTemplate<String, ChatDto> redisTemplate;

    @Mock
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    @BeforeEach
    void setUp() {
        String kurentoUrl = "ws://52.78.190.79:8888/kurento";
        kurentoClient.create(kurentoUrl);
        ReflectionTestUtils.setField(rtcChatService, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(rtcChatService, "opsHashChatRoom", opsHashChatRoom);
    }

    @Test
    @DisplayName("화상 채팅방 생성 테스트")
    void createRtcChatRoomTest() throws Exception{
        //given
        String roomName = "방1";
        //when
        KurentoRoomDto kurentoRoomDto = rtcChatService.createChatRoom(roomName, "1234", Boolean.TRUE, 50);

        //then
        assertThat(kurentoRoomDto.getRoomName()).isEqualTo(roomName);
        assertThat(kurentoRoomDto.getChatType()).isEqualTo(RTC);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));

    }
    @Test
    @DisplayName("getClients 메서드 테스트")
    void getClientsTest() {
        //given
        ChatRoom chatRoom = new ChatRoom().create("roomName", "1234", Boolean.TRUE, 50);
        // 가상의 userRtcList를 생성하고 chatRoom에 설정
        Map<String, WebSocketSession> userRtcList = new HashMap<>();
        userRtcList.put("user1", webSocketSession);
        userRtcList.put("user2", webSocketSession);

        //when
        rtcChatService.addClient(chatRoom, "user1", webSocketSession);
        rtcChatService.addClient(chatRoom, "user2", webSocketSession);
        Map<String, WebSocketSession> result = rtcChatService.getClients(chatRoom);
        //then
        assertThat(result).isEqualTo(userRtcList);


    }
    @Test
    @DisplayName("화상 채팅방 유저 참가 테스트")
    void addClientTest() throws Exception{
        //given
        String user = "이민우";
        KurentoRoomDto kurentoRoomDto = rtcChatService.createChatRoom("roomName", "1234", Boolean.TRUE, 50);
                ChatRoom chatRoom=new ChatRoom().createRtc(kurentoRoomDto.getRoomId(), kurentoRoomDto.getRoomName()
                ,kurentoRoomDto.getRoomPwd(),kurentoRoomDto.isSecretCheck(),kurentoRoomDto.getMaxUserCnt()
        );
        //when
        Map<String, WebSocketSession> userRtcList = rtcChatService.addClient(chatRoom, user, webSocketSession);

        //then
        assertThat(userRtcList.size()).isEqualTo(1);
        assertThat(userRtcList.get(user)).isEqualTo(webSocketSession);
    }

    @Test
    @DisplayName("채팅방 유저 퇴장시 이름 삭제")
    void removeClientByNameTest() throws Exception{
        //given
        String user = "이민우";
        KurentoRoomDto kurentoRoomDto = rtcChatService.createChatRoom("roomName", "1234", Boolean.TRUE, 50);
        ChatRoom chatRoom=new ChatRoom().createRtc(kurentoRoomDto.getRoomId(), kurentoRoomDto.getRoomName()
                ,kurentoRoomDto.getRoomPwd(),kurentoRoomDto.isSecretCheck(),kurentoRoomDto.getMaxUserCnt()
        );

        Map<String, WebSocketSession> userRtcList = rtcChatService.addClient(chatRoom, user, webSocketSession);

        //then
        assertThat(userRtcList.get(user)).isEqualTo(webSocketSession);
        //when
        rtcChatService.removeClientByName(chatRoom, user);
        //then
        assertThat(userRtcList.size()).isEqualTo(0);
    }
    @Test
    @DisplayName("화상 채팅방 유저 명수 체크")
    void findUserCountTest() throws Exception{
        //given
        String user = "이민우";
        String user2 = "이민우2";
        KurentoRoomDto kurentoRoomDto = rtcChatService.createChatRoom("방테스트", "1234", Boolean.TRUE, 4);
        ChatRoom chatRoom=new ChatRoom().createRtc(kurentoRoomDto.getRoomId(), kurentoRoomDto.getRoomName()
                ,kurentoRoomDto.getRoomPwd(),kurentoRoomDto.isSecretCheck(),kurentoRoomDto.getMaxUserCnt()
        );
        WebSocketMessage webSocketMessage = new WebSocketMessage();

        webSocketMessage.setData(chatRoom.getRoomId());

        rtcChatService.addClient(chatRoom, user, webSocketSession);
        rtcChatService.addClient(chatRoom, user2, webSocketSession);
        //when
        boolean userCount = rtcChatService.findUserCount(webSocketMessage);

        //then
        assertThat(userCount).isEqualTo(false);

    }
    @Test
    @DisplayName("화상 채팅방 유저 1명 아래시 체크")
    void findUserCountFailTest() throws Exception{
        //given
        String user = "이민우";
        String user2 = "이민우2";
        ChatRoom chatRoom = rtcChatService.createChatRoom("roomName", "1234", Boolean.TRUE, 50);
        WebSocketMessage webSocketMessage = new WebSocketMessage();

        webSocketMessage.setData(chatRoom.getRoomId());

        rtcChatService.addClient(chatRoom, user, webSocketSession);

        //when
        boolean userCount = rtcChatService.findUserCount(webSocketMessage);

        //then
        assertThat(userCount).isEqualTo(false);

    }
}