package com.example.VideoChatting.service;

import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.exception.ChatRoomNotFoundException;
import com.example.VideoChatting.repository.ChatRoomRepository;
import com.example.VideoChatting.service.chat.ChatRoomService;
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
    @DisplayName("채팅방 참가후 유저수 증가")
    void 채팅방유저_증가_테스트() {
        //given
        String roomName = "방21";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);
        //when
        chatRoomService.plusUserCnt(createdChatRoom.getRoomId());
        //then
        assertThat(createdChatRoom.getUserCount()).isEqualTo(1);
        verify(chatRoomRepository, times(2)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 퇴장시 유저수 체크")
    void 채팅방_유저_감소_테스트() {
        String roomName = "방감소";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);

        chatRoomService.plusUserCnt(createdChatRoom.getRoomId());
        chatRoomService.minusUserCnt(createdChatRoom.getRoomId());

        assertThat(createdChatRoom.getUserCount()).isEqualTo(0);
        verify(chatRoomRepository, times(3)).save(any(ChatRoom.class));
    }
    @Test
    @DisplayName("채팅방 유저수가 0일떄 minusUserCnt메서드를 호출해도 -1이 안되는지 체크")
    void 채팅방_유저_감소_예외테스트() {
        String roomName = "방감소";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);

        chatRoomService.minusUserCnt(createdChatRoom.getRoomId());

        assertThat(createdChatRoom.getUserCount()).isEqualTo(0);
        verify(chatRoomRepository, times(2)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("해당 채팅방 유저 참가")
    void addUser() {
        //given
        String roomName = "채팅방 유저 참가";
        String username = "이민우";
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);

        //when
        String uuid = chatRoomService.addUser(createdChatRoom.getRoomId(), username);
        //then
        assertThat(createdChatRoom.getUserList().get(uuid)).isEqualTo(username);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 유저 이름 중복 확인-중복이 되면 둘 중 하나의 유저이름에게 랜덤으로 숫자 붙이는지 확인")
    void isDuplicateName() {
        //given
        String roomName = "채팅방 중복체크";
        String username = "이민우";
        String username1 = "이민우";
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);
        //when
        chatRoomService.addUser(createdChatRoom.getRoomId(), username);
        chatRoomService.addUser(createdChatRoom.getRoomId(), username1);

        String duplicateName = chatRoomService.isDuplicateName(createdChatRoom.getRoomId(), username1);

        //then
        assertThat(username).isNotEqualTo(duplicateName);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 유저리스트에서 특정 유저 삭제")
    void delUser() {
        //given
        String roomName = "채팅방 유저 삭제";
        String username = "이민우";
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);
        String uuid = chatRoomService.addUser(createdChatRoom.getRoomId(), username);
        //when
        chatRoomService.delUser(createdChatRoom.getRoomId(), uuid);
        //then
        assertThat(createdChatRoom.getUserList().get(uuid)).isEqualTo(null);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 유저리스트에서 특정 유저 조회")
    void getUserName() {
        //given
        String roomName = "채팅방 유저 조회";
        String username = "이민우";
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);
        String uuid = chatRoomService.addUser(createdChatRoom.getRoomId(), username);
        //when
        String getUserName = chatRoomService.getUserName(createdChatRoom.getRoomId(), uuid);
        //then
        assertThat(getUserName).isEqualTo(username);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 유저리스트에서 전체 유저 조회")
    void getUserList() {
        //given
        String roomName = "채팅방 전체 유저 조회";
        String username = "이민우";
        String username1 = "이민";
        String username2 = "김민우";
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName);

        chatRoomService.addUser(createdChatRoom.getRoomId(), username);
        chatRoomService.addUser(createdChatRoom.getRoomId(), username1);
        chatRoomService.addUser(createdChatRoom.getRoomId(), username2);
        //when
        ArrayList<String> userList = chatRoomService.getUserList(createdChatRoom.getRoomId());
        //then

        assertThat(userList.size()).isEqualTo(3);

        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }
}