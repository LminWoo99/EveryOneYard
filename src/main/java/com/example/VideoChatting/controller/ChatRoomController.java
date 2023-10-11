package com.example.VideoChatting.controller;

import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.service.chat.ChatRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/")
    @ApiOperation(value = "채팅방 리스트 조회 ", notes = "전체 채팅방 목록을 조회한다")
    public ResponseEntity<List<ChatRoom>> chatRoomList() {
        List<ChatRoom> allRooms = chatRoomService.findAllRooms();
        return ResponseEntity.ok().body(allRooms);
    }
    @PostMapping("/createRoom")
    @ApiOperation(value = "채팅방 생성 ", notes = "채팅방을 생성한다")
    public ResponseEntity<ChatRoom> createRoom(@RequestParam String name) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(name);
        return ResponseEntity.ok().body(chatRoom);
    }
    @GetMapping("/{roomId}")
    @ApiOperation(value = "채팅방 입장 ", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방을 찾음 ")
    public ResponseEntity<ChatRoom> roomDetail(@PathVariable("roomId") String roomId){
        return ResponseEntity.ok().body(chatRoomService.findRoomById(roomId));

    }

}
