package com.example.VideoChatting.controller;


import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.MessageType;
import com.example.VideoChatting.service.chat.ChatRoomService;
import com.example.VideoChatting.service.chat.ChatService;
import com.example.VideoChatting.service.chat.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final SimpMessageSendingOperations template;
    private final ChatService chatService;

    private final ChatRoomService chatRoomService;
    @Value("${turn.server.urls}")
    private String turnServerUrl;

    @Value("${turn.server.username}")
    private String turnServerUserName;

    @Value("${turn.server.credential}")
    private String turnServerCredential;

    // MessageMapping 을 통해 webSocket 로 들어오는 메시지를 발신 처리한다.
    // 이때 클라이언트에서는 /pub/chat/message 로 요청하게 되고 이것을 controller 가 받아서 처리한다.
    // 처리가 완료되면 /sub/chat/room/roomId 로 메시지가 전송된다.
    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {

        // 채팅방 유저+1
        chatRoomService.plusUserCnt(chat.getRoomId());

        // 채팅방에 유저 추가 및 UserUUID 반환
        String userUUID = chatRoomService.addUser(chat.getRoomId(), chat.getSender());

        // 반환 결과를 socket session 에 userUUID 로 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
        chatRoomService.enterChatRoom(chat.getRoomId());
        chat.setMessage(chat.getSender() + " 님 입장!!");
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);

    }

    // 해당 유저
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat) {
        log.info("CHAT {}", chat);

        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisPublisher.publish(chatRoomService.getTopic(chat.getRoomId()),chat);
        chatService.saveMessage(chat);

    }
    // 대화 내역 조회
    @GetMapping("/chat/room/{roomId}/message")
    public ResponseEntity<List<ChatDto>> loadMessage(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.loadMessage(roomId));
    }


    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장을 확인
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("DisConnEvent {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        log.info("headAccessor {}", headerAccessor);

        // 채팅방 유저 -1
        chatRoomService.minusUserCnt(roomId);

        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
        String username = chatRoomService.getUserName(roomId, userUUID);
        chatRoomService.delUser(roomId, userUUID);

        if (username != null) {
            log.info("User Disconnected : " + username);

            // builder 어노테이션 활용
            ChatDto chat = ChatDto.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .message(username + " 님 퇴장!!")
                    .build();

            template.convertAndSend("/sub/chat/room/" + roomId, chat);
        }
    }

    // 채팅에 참여한 유저 리스트 반환
    @GetMapping("/chat/userlist")
    @ResponseBody
    public List<String> userList(String roomId) {

        return chatRoomService.getUserList(roomId);
    }

    // 채팅에 참여한 유저 닉네임 중복 확인
    @GetMapping("/chat/duplicateName")
    @ResponseBody
    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("username") String username) {

        // 유저 이름 확인
        String userName = chatRoomService.isDuplicateName(roomId, username);
        log.info("동작확인 {}", userName);

        return userName;
    }
    // turn server config
    @PostMapping("/turnconfig")
    @ResponseBody
    public Map<String, String> turnServerConfig(){
        Map<String, String> turnServerConfig = new HashMap<>();
        turnServerConfig.put("url", turnServerUrl);
        turnServerConfig.put("username", turnServerUserName);
        turnServerConfig.put("credential", turnServerCredential);

        return turnServerConfig;
    }
}

