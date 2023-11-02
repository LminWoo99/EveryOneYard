package com.example.VideoChatting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId; // 채팅방 아이디
    private String roomName; // 채팅방 이름
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int userCount; // 채팅방 인원수

    private int maxUserCnt;

    private String roomPwd;

    private static final long serialVersionUID = 6494678977089006639L;

    private boolean secretCheck;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    private HashMap<String, String> userList = new HashMap<String, String>();
    private HashMap<String, WebSocketSession> userRtcList = new HashMap<String, WebSocketSession>();
    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public ChatRoom create(String roomName, String roomPwd, boolean secretCheck, int maxUserCnt){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;
        chatRoom.maxUserCnt = maxUserCnt;
        chatRoom.roomPwd = roomPwd;
        chatRoom.secretCheck = secretCheck;
        return chatRoom;
    }

    public void setUserRtcList(HashMap<String, WebSocketSession> userRtcList) {
        this.userRtcList = userRtcList;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public void addChatMessages(ChatMessage chatMessage) {
        this.chatMessageList.add(chatMessage);
    }
}
