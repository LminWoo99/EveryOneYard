

package com.example.VideoChatting.dto;

import com.example.VideoChatting.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto implements Serializable {
    /*메시지  타입 : 입장, 채팅
      메시지 타입에 따라서 동작하는 구조가 달라진다.
      입장과 퇴장 ENTER 과 LEAVE 의 경우 입장/퇴장 이벤트 처리가 실행되고,
      TALK 는 말 그대로 내용이 해당 채팅방을 SUB 하고 있는 모든 클라이언트에게 전달된다.*/
    public enum MessageType{
        ENTER, TALK, LEAVE;
    }
    private MessageType type; // 메시지 타입
    private String roomId; // 방 번호
    private String sender; // 채팅을 보낸 사람
    private String message; // 메시지
    private Date createdAt; // 채팅 발송 시간

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

    public ChatDto(ChatMessage chatMessage) {
        this.sender = chatMessage.getSender();
        this.type = MessageType.TALK;
        this.createdAt = chatMessage.getCreatedAt();
        this.roomId = chatMessage.getChatRoom().getRoomId();
        this.message = chatMessage.getMessage();
        this.s3DataUrl= chatMessage.getS3DataUrl();
    }

}

