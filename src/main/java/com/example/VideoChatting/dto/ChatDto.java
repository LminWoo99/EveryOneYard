

package com.example.VideoChatting.dto;

import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.MessageType;
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

    private MessageType type; // 메시지 타입
    private String roomId; // 방 번호
    private String sender; // 채팅을 보낸 사람
    private String message; // 메시지
    private Date createdAt; // 채팅 발송 시간

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

    public ChatMessage toEntity() {
        return ChatMessage.builder()
                .type(type)
                .sender(sender)
                .message(message)
                .s3DataUrl(s3DataUrl)
                .roomId(roomId)
                .fileName(fileName)
                .fileDir(fileDir)
                .build();
    }


}

