package com.example.VideoChatting.controller;

import com.example.VideoChatting.dto.WebSocketMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.service.chat.RtcChatService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RtcChatController {
    private final RtcChatService rtcChatService;

    @PostMapping("/webrtc/usercount")
    private ResponseEntity<String> webRTC(@ModelAttribute WebSocketMessage webSocketMessage) {
        log.info("MESSAGE : {}", webSocketMessage.getData());
        return ResponseEntity.ok().body(Boolean.toString(rtcChatService.findUserCount(webSocketMessage)));
    }
    @PostMapping("/chat/createRtcRoom")
    @ApiOperation(value = "채팅방 생성 ", notes = "채팅방을 생성한다")
    public String createRoom(@RequestParam("roomName") String name, @RequestParam("roomPwd")String roomPwd, @RequestParam("secretCheck")String secretCheck,
                             @RequestParam(value = "maxUserCnt", defaultValue = "100")String maxUserCnt, RedirectAttributes rttr) {
        ChatRoom room = rtcChatService.createChatRoom(name, roomPwd, Boolean.parseBoolean(secretCheck), Integer.parseInt(maxUserCnt));
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/chat";
    }
}
