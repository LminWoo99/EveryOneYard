package com.example.VideoChatting.entity;

import com.example.VideoChatting.dto.ChatDto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MessageType type;
    private String sender;
    private String message;
    private String s3DataUrl;
    private String roomId;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
    private String fileName; // 파일이름

    private String fileDir; // s3 파일 경로

    public ChatMessage(String sender, String message, String s3DataUrl, ChatRoom chatRoom, String roomId, String fileName, String fileDir) {
        this.sender = sender;
        this.message = message;
        this.s3DataUrl = s3DataUrl;
        this.roomId = roomId;
        this.fileName = fileName;
        this.fileDir = fileDir;
    }

    public static ChatMessage createChatMessage(ChatRoom chatRoom, String sender, String message, MessageType type, String s3DataUrl) {
        ChatMessage chatMessage= ChatMessage.builder()
                .sender(sender)
                .message(message)
                .type(type)
                .s3DataUrl(s3DataUrl)
                .build();
        return chatMessage;
    }
    public ChatDto toDto() {
        return ChatDto.builder()
                .type(type)
                .roomId(roomId)
                .sender(sender)
                .message(message)
                .createdAt(createdAt)
                .s3DataUrl(s3DataUrl)
                .fileName(fileName)
                .fileDir(fileDir)
                .build();
    }
    public void setMessage(String message){
        this.message=message;
    }

}

