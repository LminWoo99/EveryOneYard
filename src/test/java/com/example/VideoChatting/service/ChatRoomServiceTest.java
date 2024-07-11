package com.example.VideoChatting.service;

import com.example.VideoChatting.entity.ChatMessage;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.cert.TrustAnchor;
import java.util.*;

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


    @Test
    @DisplayName("방리스트 조회")
    void 방리스트조회_test() {
        //given
        List<ChatRoom> chatRooms = new ArrayList<>();
        String roomName = "방1";
        chatRooms.add(new ChatRoom().create(roomName, "1234", Boolean.TRUE, 50));
        String roomName2 = "방12";
        chatRooms.add(new ChatRoom().create(roomName2, "1234", Boolean.TRUE, 50));
        //stub
        when(chatRoomService.findAllRooms()).thenReturn(chatRooms);
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
            ChatRoom chatRoom = new ChatRoom().create(roomName, "1234", Boolean.TRUE, 50);
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
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName, "1234", Boolean.TRUE, 50);
        // Then
        assertThat(createdChatRoom.getRoomName()).isEqualTo(roomName);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));



    }
    @Test
    @DisplayName("똑같은 방 이름 생성시 예외 터뜨리는지 테스트")
    void 똑같은_방_이름생성_테스트() {
        //given
        String roomName = "방1";
        //when
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName, "1234", Boolean.TRUE, 50);
        when(chatRoomRepository.existsByRoomName(roomName)).thenReturn(Boolean.TRUE);
        // Then
        assertThrows(ChatRoomNotFoundException.class, () -> {
            chatRoomService.createChatRoom(roomName,"1234", Boolean.TRUE, 50);
        });
        // Then
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 참가후 유저수 증가")
    void 채팅방유저_증가_테스트() {
        //given
        String roomName = "방21";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName, "1234", Boolean.TRUE, 50);
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

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName, "1234", Boolean.TRUE, 50);
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

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomName, "1234", Boolean.TRUE, 50);
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
        String roomId = UUID.randomUUID().toString();;
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);

        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(createdChatRoom);
        //when
        String uuid = chatRoomService.addUser(roomId, username);
        //then
        assertThat(createdChatRoom.getUserList().get(uuid)).isEqualTo(username);
        verify(chatRoomRepository, times(1)).findByRoomId(roomId);
    }
    @Test
    @DisplayName("중복되는 이름이 없을 떄")
    void isDuplicateName_UniqueUsername_ReturnsOriginalName() {
        //given
        String roomName = "채팅방 유저 참가";
        String username = "이민우";
        String roomId = UUID.randomUUID().toString();;

        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);


        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(createdChatRoom);
        //when
        String result = chatRoomService.isDuplicateName(roomId, username);
        //then
        assertEquals(username, result);
        verify(chatRoomRepository).findByRoomId(roomId);
    }

    @Test
    @DisplayName("중복되는 이름이 있을 떄")
    void isDuplicateName_DuplicateUsername_ReturnsModifiedName() {
        //given
        String roomName = "채팅방 유저 참가";
        String username = "이민우";
        String roomId = UUID.randomUUID().toString();;

        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);

        createdChatRoom.getUserList().put("1232", username);

        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(createdChatRoom);
        //when
        String result = chatRoomService.isDuplicateName(roomId, username);
        //then
        assertNotEquals(username, result);
        assertTrue(result.startsWith(username));
        verify(chatRoomRepository).findByRoomId(roomId);
    }

    @Test
    @DisplayName("중복되는 이름이 여러번 있을 떄")
    void isDuplicateName_MultipleAttempts_ReturnsUniqueModifiedName() {
        //given
        String roomName = "채팅방 유저 참가";
        String username = "이민우";
        String roomId = UUID.randomUUID().toString();;

        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);

        createdChatRoom.getUserList().put("uuid1", username);
        createdChatRoom.getUserList().put("uuid2", username + "1");
        createdChatRoom.getUserList().put("uuid3", username + "2");
        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(createdChatRoom);
        //when
        String result = chatRoomService.isDuplicateName(roomId, username);
        //then
        assertNotEquals(username, result);
        assertNotEquals(username + "1", result);
        assertNotEquals(username + "2", result);
        assertTrue(result.startsWith(username));
        verify(chatRoomRepository).findByRoomId(roomId);
    }


    @Test
    @DisplayName("채팅방 유저리스트에서 특정 유저 삭제")
    void delUser() {
        //given
        String roomName = "채팅방 유저 삭제";
        String username = "이민우";

        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);

        createdChatRoom.getUserList().put("uuid1", username);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);
        //when
        chatRoomService.delUser(createdChatRoom.getRoomId(), "uuid1");
        //then
        assertThat(createdChatRoom.getUserList().get("uuid1")).isEqualTo(null);
        assertThat(createdChatRoom.getUserList().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("채팅방 유저리스트에서 특정 유저 조회")
    void getUserName() {
        //given
        String roomName = "채팅방 유저 참가";
        String username = "이민우";
        String userUUID=UUID.randomUUID().toString();


        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);

        createdChatRoom.getUserList().put(userUUID, username);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);
        //when
        String getUserName = chatRoomService.getUserName(createdChatRoom.getRoomId(), userUUID);
        //then
        assertThat(getUserName).isEqualTo(username);
    }

    @Test
    @DisplayName("채팅방 유저리스트에서 전체 유저 조회")
    void getUserList() {
        //given
        String roomName = "채팅방 유저 참가";
        String username = "이민우";

        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);

        createdChatRoom.getUserList().put("uuid1", username);
        createdChatRoom.getUserList().put("uuid2", username + "1");
        createdChatRoom.getUserList().put("uuid3", username + "2");
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);
        //when
        List<String> userList = chatRoomService.getUserList(createdChatRoom.getRoomId());
        //then

        assertThat(userList.size()).isEqualTo(3);
    }
    @Test
    @DisplayName("채팅방 삭제")
    void deleteChatRoom() throws Exception{
        //given
        String roomName = "채팅방 유저 삭제";
        String username = "이민우";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);

        //when
        chatRoomService.deleteChatRoom(createdChatRoom.getRoomId());

        //then
        assertThat(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).isEqualTo(null);
    }
    @Test
    @DisplayName("채팅방 최대 인원 체크 테스트")
    void checkRoomUserCntTest() throws Exception{
        //given
        String roomName = "채팅방 유저 삭제";
        String username = "이민우";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);
        //when
        boolean test = chatRoomService.checkRoomUserCnt(createdChatRoom.getRoomId());
        //then
        assertThat(test).isEqualTo(Boolean.TRUE);
        verify(chatRoomRepository, times(1)).findByRoomId(createdChatRoom.getRoomId());
    }
    @Test
    @DisplayName("채팅방 최대 인원 초과 체크 테스트")
    void checkRoomUserCntUpTest() throws Exception{
        //given
        String roomName = "채팅방 유저 삭제";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 1);


        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);

        chatRoomService.plusUserCnt(createdChatRoom.getRoomId());
        chatRoomService.plusUserCnt(createdChatRoom.getRoomId());

        //when
        boolean test = chatRoomService.checkRoomUserCnt(createdChatRoom.getRoomId());

        //then
        assertThat(test).isEqualTo(Boolean.FALSE);
        verify(chatRoomRepository, times(3)).findByRoomId(createdChatRoom.getRoomId());
    }
    @Test
    @DisplayName("채팅방 비밀번호 체크 테스트")
    void confirmPwdTest() throws Exception{
        //given
        String roomName = "채팅방 비밀번호 체크";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);
        //when
        boolean check = chatRoomService.confirmPwd(createdChatRoom.getRoomId(), "1234");
        //then
        assertThat(check).isEqualTo(Boolean.TRUE);

    }
    @Test
    @DisplayName("채팅방 비밀번호 체크 실패 테스트")
    void confirmPwdFailTest() throws Exception{
        //given
        String roomName = "채팅방 비밀번호 체크";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);
        when(chatRoomRepository.findByRoomId(createdChatRoom.getRoomId())).thenReturn(createdChatRoom);
        //when
        boolean check = chatRoomService.confirmPwd(createdChatRoom.getRoomId(), "1234dsdsds");
        //then
        assertThat(check).isEqualTo(Boolean.FALSE);
    }
    @Test
    @DisplayName("채팅방 이름 수정 단위 테스트")
    void updateRoomNameTest() throws Exception{
        //given
        String roomName = "채팅방 이름  수정";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);
        String updateRoomName = "수정된 채팅방 이름";

        //when
        chatRoomService.updateRoomName(createdChatRoom.getRoomId(), updateRoomName);

        //then
        verify(chatRoomRepository, times(1)).updateRoomName(createdChatRoom.getRoomId(), updateRoomName);

    }
    @Test
    @DisplayName("채팅방 패스워드 수정 단위 테스트")
    void updateRoomPasswordTest() throws Exception{
        //given
        String roomName = "채팅방 pw  수정";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);
        String updateRoomPw = "수정된 채팅방 pw";

        doNothing().when(chatRoomRepository).updateRoomPwd(anyString(), anyString());
        //when
        chatRoomService.updateRoomPassWord(createdChatRoom.getRoomId(), updateRoomPw);

        //then

        verify(chatRoomRepository, times(1)).updateRoomPwd(createdChatRoom.getRoomId(), updateRoomPw);

    } @Test
    @DisplayName("채팅방 잠금 상태 수정 단위 테스트")
    void updateRoomSecretCheckTest() throws Exception{
        //given
        String roomName = "채팅방 상태 수정";
        ChatRoom createdChatRoom = ChatRoom.create(roomName, "1234", Boolean.TRUE, 100);
        String updateSecretCheck = "true";

        //when
        chatRoomService.updateRoomSecretCheck(createdChatRoom.getRoomId(), updateSecretCheck);

        //then
        verify(chatRoomRepository, times(1)).updateRoomSecretCheck(createdChatRoom.getRoomId(), Boolean.valueOf(updateSecretCheck));

    }


}