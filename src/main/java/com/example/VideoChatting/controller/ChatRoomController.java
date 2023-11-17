package com.example.VideoChatting.controller;

import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.ChatType;
import com.example.VideoChatting.entity.ChatUser;
import com.example.VideoChatting.entity.SessionUser;
import com.example.VideoChatting.service.chat.ChatRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("")
    @ApiOperation(value = "채팅방 리스트 조회 ", notes = "전체 채팅방 목록을 조회한다")
    public String chatRoomList(Model model, @AuthenticationPrincipal SessionUser sessionUser) {
        model.addAttribute("list", chatRoomService.findAllRooms());
        log.info("채팅방 설정 =" + chatRoomService.findAllRooms());
        // 세션에 저장된 로그인 정보를 모델에 추가
        if (sessionUser != null) {
            model.addAttribute("user", sessionUser.getAttributes());
        }
        return "roomlist";
    }

    @PostMapping("/createRoom")
    @ApiOperation(value = "채팅방 생성 ", notes = "채팅방을 생성한다")
    public String createRoom(@RequestParam("roomName") String name, @RequestParam("roomPwd")String roomPwd, @RequestParam("secretCheck")String secretCheck,
                             @RequestParam(value = "maxUserCnt")String maxUserCnt,  RedirectAttributes rttr) {
        ChatRoom room = chatRoomService.createChatRoom(name, roomPwd, Boolean.parseBoolean(secretCheck), Integer.parseInt(maxUserCnt));
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/chat";
    }

    @GetMapping("/room")
    @ApiOperation(value = "채팅방 입장 ", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방을 찾음 ")
    public String roomDetail(Model model, String roomId) {
        log.info("roomId {}", roomId);
        ChatRoom chatRoom = chatRoomService.findRoomById(roomId);
        model.addAttribute("room", chatRoom);

        if (chatRoom.getChatType().equals(ChatType.MSG)) {
            return "chatroom";
        } else {
            log.info("화상채팅 유저 확인 :" + chatRoom.getUserRtcList());
//            model.addAttribute("uuid", UUID.randomUUID().toString());
            String uuid = UUID.randomUUID().toString().split("-")[0];
            model.addAttribute("uuid", uuid);
            model.addAttribute("roomId", chatRoom.getRoomId());
            model.addAttribute("roomName", chatRoom.getRoomName());
            return "kurentoroom";
        }
    }

    @PostMapping("/confirmPwd/{roomId}")
    @ResponseBody
    @ApiOperation(value = "채팅방 비밀번호 체크 ", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방 비밀 번호 체크 ")
    public boolean confirmPwd(@PathVariable String roomId, @RequestParam String roomPwd) {

        // 넘어온 roomId 와 roomPwd 를 이용해서 비밀번호 찾기
        // 찾아서 입력받은 roomPwd 와 room pwd 와 비교해서 맞으면 true, 아니면  false
        return chatRoomService.confirmPwd(roomId, roomPwd);
    }

    @GetMapping("/delRoom/{roomId}")
    @ApiOperation(value = "채팅방 삭제 ", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방 삭제 ")
    public String deleteChatRoom(@PathVariable String roomId) {

        // roomId 기준으로 chatRoomMap 에서 삭제, 해당 채팅룸 안에 있는 사진 삭제
        chatRoomService.deleteChatRoom(roomId);


        return "redirect:/chat";
    }

    // 유저 카운트
    @GetMapping("/chkUserCnt/{roomId}")
    @ResponseBody
    @ApiOperation(value = "채팅방 유저 카운트 ", notes = "사용자가 설정한 최대 인원 안 넘는지 체크  ")
    public boolean checkUserCnt(@PathVariable String roomId) {
        return chatRoomService.checkRoomUserCnt(roomId);
    }

    @PutMapping("/updateRoomName/{roomId}")
    @ApiOperation(value = "채팅방 이름 변경", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방 이름 변경")
    public ResponseEntity<?> updateRoomName(@PathVariable String roomId, @RequestBody Map<String, String> body) {
        String roomName = body.get("roomName");
        chatRoomService.updateRoomName(roomId, roomName);
        return ResponseEntity.ok().build(); // JSON 형태의 응답을 보내거나, 상태 코드만 보냅니다.
    }

    @PutMapping("/updateRoomPwd/{roomId}")
    @ApiOperation(value = "채팅방 비번 변경", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방 비번 변경")
    public ResponseEntity<?> updateRoomPwd(@PathVariable String roomId, @RequestBody Map<String, String> body) {
        String roomPwd = body.get("roomPwd");
        chatRoomService.updateRoomPassWord(roomId, roomPwd);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/updateRoomSecret/{roomId}")
    @ApiOperation(value = "채팅방 잠금 상태 변경", notes = "파라미터로 넘어오는 ROOMID 기준으로 채팅방 잠금/오픈 상태 변경")
    public ResponseEntity<?> updateRoomSecretCheck(@PathVariable String roomId, @RequestBody Map<String, String> body) {
        String secretCheck = body.get("secretCheck");
        chatRoomService.updateRoomSecretCheck(roomId, secretCheck);
        return ResponseEntity.ok().build();
    }
}
