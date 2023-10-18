package com.example.VideoChatting.entity;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.dto.ChatDto.MessageType;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MessageType type;
    private String sender;
    private String message;
    private String s3DataUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHATROOM_ID")
    private ChatRoom chatRoom;

    public static ChatMessage createChatMessage(ChatRoom chatRoom, String sender, String message, MessageType type, String s3DataUrl) {
        ChatMessage chatMessage= ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(message)
                .type(type)
                .s3DataUrl(s3DataUrl)
                .build();
        return chatMessage;
    }
    public void setSender(String sender){
        this.sender=sender;
    }

    public void setMessage(String message){
        this.message=message;
    }

}
