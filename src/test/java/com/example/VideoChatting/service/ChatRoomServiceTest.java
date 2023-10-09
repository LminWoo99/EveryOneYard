package com.example.VideoChatting.service;

import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.exception.ChatRoomNotFoundException;
import com.example.VideoChatting.repository.ChatRoomRepository;
import com.example.VideoChatting.repository.ChatUserRepository;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    @InjectMocks
    private ChatRoomService chatRoomService;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    private Map<String, ChatRoom> chatRoomMap;
    @BeforeEach
    public void setup() {
        chatRoomMap = new LinkedHashMap<>();
        ReflectionTestUtils.setField(chatRoomService, "chatRoomMap", chatRoomMap);
    }

    @Test
    @DisplayName("방리스트 조회")
    void 방리스트조회_test() {

        //given
        List<ChatRoom> chatRooms = new ArrayList<>();
        String roomName = "방1";
        chatRooms.add(new ChatRoom().create(roomName));
        String roomName2 = "방12";
        chatRooms.add(new ChatRoom().create(roomName2));
        chatRoomRepository.saveAll(chatRooms);
        //stub
        when(chatRoomRepository.findAll()).thenReturn(chatRooms);
        //when
        List<ChatRoom> allRooms = chatRoomService.findAllRooms();

        //then
        assertThat(allRooms.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("방이름으로 조회")
    void 방_아이디_검색_test() {
            //given

            String roomName = "방1";
            ChatRoom chatRoom = new ChatRoom().create(roomName);
            chatRoomRepository.save(chatRoom);
            //stub
            when(chatRoomRepository.findByRoomId(roomName)).thenReturn(chatRoom);
            ChatRoom roomById = chatRoomService.findRoomById(roomName);

            //then
             assertThat(roomById.getRoomName()).isEqualTo("방1");
             verify(chatRoomRepository, times(1)).findByRoomId(roomName);
    }

    @Test
    @DisplayName("방 생성후 채팅방 리스트 확인")
    void 방생성_테스트() {
        //given

        String roomName = "방1";
        //when
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);

        // Then
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));

        assertNotNull(createdChatRoom);

        assertTrue(chatRoomMap.containsKey(createdChatRoom.getRoomId()));
        assertEquals(createdChatRoom, chatRoomMap.get(createdChatRoom.getRoomId()));
        assertThat(chatRoomMap.get(createdChatRoom.getRoomId()).getRoomName()).isEqualTo("방1");
    }
    @Test
    @DisplayName("똑같은 방 이름 생성시 예외 터뜨리는지 테스트")
    void 똑같은_방_이름생성_테스트() {
        //given
        String roomName = "방1";
        //when
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);
        when(chatRoomRepository.existsByRoomName(roomName)).thenReturn(Boolean.TRUE);
        // Then
        assertThrows(ChatRoomNotFoundException.class, () -> {
            chatRoomService.createChatRoom(roomName);
        });
        // Then
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void plusUserCnt() {
    }

    @Test
    void minusUserCnt() {
    }

    @Test
    void addUser() {
    }

    @Test
    void isDuplicateName() {
    }

    @Test
    void delUser() {
    }

    @Test
    void getUserName() {
    }

    @Test
    void getUserList() {
    }
}