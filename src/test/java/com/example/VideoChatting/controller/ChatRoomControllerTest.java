package com.example.VideoChatting.controller;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.ChatType;
import com.example.VideoChatting.entity.ChatUser;
import com.example.VideoChatting.entity.SessionUser;
import com.example.VideoChatting.repository.ChatRoomRepository;
import com.example.VideoChatting.repository.ChatUserRepository;
import com.example.VideoChatting.service.chat.ChatRoomService;
import com.example.VideoChatting.service.chat.RtcChatService;
import com.example.VideoChatting.service.oAuth.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;


import java.util.*;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomControllerTest {
    @InjectMocks
    private ChatRoomController chatRoomController;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private RtcChatService rtcChatService;
    private MockMvc mockMvc;
    @Mock
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    @BeforeEach
    public void init() { // mockMvc 초기화, 각메서드가 실행되기전에 초기화 되게 함
        ReflectionTestUtils.setField(chatRoomService, "opsHashChatRoom", opsHashChatRoom);
        mockMvc = MockMvcBuilders.standaloneSetup(chatRoomController).build();
    }
    @Test
    @WithMockCustomUser(nickname = "testUser", email = "test@example.com")
    @DisplayName("채팅방 리스트 조회 컨트롤러 테스트 엔드포인트 : /chat ")
    void chatRoomList() throws Exception {
        List<ChatRoom> mockRooms = new ArrayList<>();
        mockRooms.add(new ChatRoom().create("Room1", "1234", Boolean.TRUE, 50));
        mockRooms.add(new ChatRoom().create("Room2", "1234", Boolean.TRUE, 50));

        // chatRoomService.findAllRooms()가 반환할 목업 데이터 설정
        when(chatRoomService.findAllRooms()).thenReturn(mockRooms);


        mockMvc.perform(get("/chat"))
                .andExpect(status().isOk())
                .andExpect(view().name("roomlist")) // 예상하는 뷰 이름
                .andExpect(model().attributeExists("list", "user")); // 모델 속성 검증

        // chatRoomService.findAllRooms() 메서드가 호출되었는지 확인
        verify(chatRoomService, times(2)).findAllRooms();

    }
    @Test
    @DisplayName("채팅방 생성 요청시 컨트롤러 테스트")
    void createRoom() throws Exception {
        //given
        String roomName = "방생성요청";
        ChatRoom chatRoom = new ChatRoom().create(roomName, "1234", Boolean.TRUE, 50);
        when(chatRoomService.createChatRoom(roomName, "1234", Boolean.TRUE, 50)).thenReturn(chatRoom);
        //when
        ResultActions perform = mockMvc.perform(post("/chat/createRoom")
                .param("roomName", roomName)
                .param("roomPwd", "1234")
                .param("secretCheck", "True")
                .param("maxUserCnt", "50")
        );

        //then
        perform.andExpect(status().is3xxRedirection());
    }
    @Test
    @DisplayName("채팅방 생성 요청시 이름 파라미터 안넘기는 컨트롤러 실패 테스트")
    void failCreateRoom() throws Exception {
        //given
        ChatRoom chatRoom = new ChatRoom();

        //when
        ResultActions perform = mockMvc.perform(post("/chat/createRoom")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("일반 채팅방 입장 컨트롤러 테스트")
    void roomDetail() throws Exception {
        //given
        String roomName = "채팅방 입장";
        String roomId="방1";
        ChatRoom chatRoom = new ChatRoom().create(roomName, "1234", Boolean.TRUE, 50);

        chatRoomService.createChatRoom(roomId, "1234", Boolean.TRUE, 50);
        chatRoom.setChatType(ChatType.MSG);

        when(chatRoomService.findRoomById(roomId)).thenReturn(chatRoom);

        //when
        ResultActions perform = mockMvc.perform(get("/chat/room")
                .param("roomId", roomId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(view().name("chatroom"));

    }
    @Test
    @DisplayName("화상 채팅방 입장 컨트롤러 테스트")
    void rtcRoomDetail() throws Exception {
        //given
        String roomName = "화상 채팅방 테스트";
        String roomId = "화상 채팅방1";
        ChatRoom chatRoom = new ChatRoom().create(roomName, "1234", Boolean.TRUE, 50);

        ChatRoom chatRoom1 = rtcChatService.createChatRoom(roomId, "1234", Boolean.TRUE, 50);
        chatRoom.setChatType(ChatType.RTC);

        when(chatRoomService.findRoomById(roomId)).thenReturn(chatRoom);

        //when
        ResultActions perform = mockMvc.perform(get("/chat/room")
                .param("roomId", roomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());


        //then
        perform.andExpect(status().isOk())
                .andExpect(view().name("rtcroom"))
                .andExpect(model().attributeExists("room"));
    }
    @Test
    @DisplayName("채팅방 비밀번호 체크 컨트롤러 테스트")
    void confirmPwdTest() throws Exception{
        //given
        String roomName = "채팅방 입장";
        String roomId="room1";
        String roomPwd = "1234";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomId, roomPwd, Boolean.TRUE, 50);
        when(chatRoomService.confirmPwd(roomId, roomPwd)).thenReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc.perform(post("/chat/confirmPwd/{roomId}" , roomId)
                .param("roomPwd", "1234")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
    @Test
    @DisplayName("채팅방 비밀번호 체크  컨트롤러 실패 테스트")
    void confirmPwdFailTest() throws Exception{
        //given
        String roomName = "채팅방 입장";
        String roomId="room1";
        String roomPwd = "1234";
        String wrongRoomPwd = "12345";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomId, roomPwd, Boolean.TRUE, 50);
        when(chatRoomService.confirmPwd(roomId, wrongRoomPwd)).thenReturn(Boolean.FALSE);
        //when
        ResultActions perform = mockMvc.perform(post("/chat/confirmPwd/{roomId}" , roomId)
                .param("roomPwd", wrongRoomPwd)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
    @Test
    @DisplayName("채팅방 삭제 컨트롤러 테스트")
    void deleteChatRoomTest() throws Exception{
        //given
        String roomId="room1";
        String roomPwd = "1234";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomId, roomPwd, Boolean.TRUE, 50);

        //when
        ResultActions perform = mockMvc.perform(get("/chat/delRoom/{roomId}" , roomId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is3xxRedirection());
    }
    @Test
    void checkUserCnt() throws Exception{
        //given
        String roomId="room1";
        String roomPwd = "1234";

        ChatRoom createdChatRoom = chatRoomService.createChatRoom(roomId, roomPwd, Boolean.TRUE, 1);

        when(chatRoomService.checkRoomUserCnt(roomId)).thenReturn(Boolean.TRUE);
        //when
        ResultActions perform = mockMvc.perform(get("/chat/chkUserCnt/{roomId}" , roomId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(chatRoomService, times(1)).checkRoomUserCnt(roomId);
    }
}