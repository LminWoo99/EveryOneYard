package com.example.VideoChatting.controller;

import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.ChatUser;
import com.example.VideoChatting.entity.SessionUser;
import com.example.VideoChatting.service.chat.ChatRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final HttpSession httpSession;
        @GetMapping("")
        @ApiOperation(value = "채팅방 리스트 조회 ", notes = "전체 채팅방 목록을 조회한다")
        public String chatRoomList(Model model, @AuthenticationPrincipal SessionUser sessionUser) {
            model.addAttribute("list",chatRoomService.findAllRooms());
            log.info("채팅방 설정 =" + chatRoomService.findAllRooms());
            // 세션에 저장된 로그인 정보를 모델에 추가
            if (sessionUser != null) {
                model.addAttribute("user", sessionUser.getAttributes());
            }
            log.info("유저 정보 =" + sessionUser.getAttributes());
            return "roomlist";
        }
        @GetMapping("/json")
        @ApiOperation(value = "채팅방 리스트 조회 json 반환", notes = "전체 채팅방 목록을 조회한다")
        @ResponseBody
        public List<ChatRoom> chatRoomList() {
            return chatRoomService.findAllRooms();

        }
    @PostMapping("/createRoom")
    @ApiOperation(value = "채팅방 생성 ", notes = "채팅방을 생성한다")
    public String createRoom(@RequestParam String roomName, RedirectAttributes rttr) {
        ChatRoom room = chatRoomService.createChatRoom(roomName);
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/chat";
    }
    @GetMapping("/room")
    @ApiOperation(value = "채팅방 입장 ", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방을 찾음 ")
    public String roomDetail(Model model,  String roomId){
        log.info("roomId {}", roomId);
        model.addAttribute("room", chatRoomService.findRoomById(roomId));
        return "chatroom";
    }

}
