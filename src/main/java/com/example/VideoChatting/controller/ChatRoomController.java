package com.example.VideoChatting.controller;

import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.service.chat.ChatRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/")
    @ApiOperation(value = "채팅방 리스트 조회 ", notes = "전체 채팅방 목록을 조회한다")
    public String chatRoomList(Model model) {
        model.addAttribute("list",chatRoomService.findAllRooms());
        return "roomList";
    }
    @PostMapping("/createRoom")
    @ApiOperation(value = "채팅방 생성 ", notes = "채팅방을 생성한다")
    public String createRoom(@RequestParam String roomName, RedirectAttributes rttr) {
        ChatRoom room = chatRoomService.createChatRoom(roomName);
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/chat/";
    }
    @GetMapping("/{roomId}")
    @ApiOperation(value = "채팅방 입장 ", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방을 찾음 ")
    public String roomDetail(Model model, @PathVariable String roomId){
        model.addAttribute("room", chatRoomService.findRoomById(roomId));
        return "chatroom";
    }

}
