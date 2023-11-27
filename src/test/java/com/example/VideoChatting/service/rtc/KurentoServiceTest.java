package com.example.VideoChatting.service.rtc;

import com.example.VideoChatting.dto.ChatRoomMap;
import com.example.VideoChatting.dto.KurentoRoomDto;
import com.example.VideoChatting.entity.ChatRoom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KurentoServiceTest {
    @InjectMocks
    KurentoService kurentoService;
    @Mock
    ConcurrentMap<String, ChatRoom> rooms;


    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(kurentoService, "rooms", rooms);
    }
    @Test
    @DisplayName("roomName기준으로 방 정보 가져오기")
    void getRoom() {
        //givem
        KurentoRoomDto fakeRoom = new KurentoRoomDto("roomId");
        when(rooms.get("roomId")).thenReturn(fakeRoom);

        // when
        KurentoRoomDto room = kurentoService.getRoom("roomId");

        // then
        assertNotNull(room);
        assertEquals("roomId", room.getRoomId());
    }
    @Test
    @DisplayName("방이 존재하지 않을 시 방 정보 가져오기")
    void nonExistingRoom() {
        //givem
        when(rooms.get("nonExistingRoomId")).thenReturn(null);
        //when
        KurentoRoomDto room = kurentoService.getRoom("nonExistingRoomId");

        // then
        assertNull(room);
    }

    @Test
    @DisplayName("화상 채팅방 삭제 테스트")
    void removeRoom() throws IOException {
        //given
        KurentoRoomDto kurentoRoomDto = mock(KurentoRoomDto.class);
        doNothing().when(kurentoRoomDto).close();
        when(rooms.remove(null)).thenReturn(kurentoRoomDto);
        //when
//        KurentoRoomDto room = kurentoService.getRoom("roomId");
        kurentoService.removeRoom(kurentoRoomDto);

        //then

        assertDoesNotThrow(() -> kurentoService.removeRoom(kurentoRoomDto));


    }
}