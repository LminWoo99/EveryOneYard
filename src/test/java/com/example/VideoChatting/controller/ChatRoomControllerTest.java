package com.example.VideoChatting.controller;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.service.chat.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatRoomControllerTest {
    @InjectMocks
    private ChatRoomController chatRoomController;
    @Mock
    private ChatRoomService chatRoomService;
    private Map<String, ChatRoom> chatRoomMap;
    private MockMvc mockMvc;
    @BeforeEach
    public void init() { // mockMvc 초기화, 각메서드가 실행되기전에 초기화 되게 함
        mockMvc = MockMvcBuilders.standaloneSetup(chatRoomController).build();
    }
    @Test
    @DisplayName("채팅방 리스트 조회 컨트롤러 테스트 엔드포인트 : /chat ")
    void chatRoomList() throws Exception {
        //given
        String roomName = "방1";
        String roomName1 = "방2";
        ChatRoom chatRoom = new ChatRoom().create(roomName);
        ChatRoom chatRoom1 = new ChatRoom().create(roomName1);

        List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();
        chatRoomList.add(chatRoom);
        chatRoomList.add(chatRoom1);

        when(chatRoomService.findAllRooms()).thenReturn(chatRoomList);

        ResultActions perform = mockMvc.perform(get("/chat/").contentType(MediaType.APPLICATION_JSON));
         //then
        perform.andExpect(status().isOk())
                .andExpect(content().json("[{\"roomName\":\"방1\"},{\"roomName\":\"방2\"}]"));
    }
    @Test
    @DisplayName("채팅방 생성 요청시 컨트롤러 테스트")
    void createRoom() throws Exception {
        //given
        String roomName = "방생성요청";
        ChatRoom chatRoom = new ChatRoom().create(roomName);
        when(chatRoomService.createChatRoom(roomName)).thenReturn(chatRoom);
        //when
        ResultActions perform = mockMvc.perform(post("/chat/createRoom")
                .param("name", roomName)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(content().json("{\"roomName\":\"방생성요청\"}"));
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
    @DisplayName("채팅방 입장 테스트")
    void roomDetail() throws Exception {
        //given
        String roomName = "채팅방 입장";
        ChatRoom chatRoom = new ChatRoom().create(roomName);
        when(chatRoomService.findRoomById(chatRoom.getRoomId())).thenReturn(chatRoom);

        //when
        ResultActions perform = mockMvc.perform(get("/chat/"+chatRoom.getRoomId())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        perform.andExpect(status().isOk())
                .andExpect(content().json("{\"roomName\":\"채팅방 입장\"}"));
    }
}