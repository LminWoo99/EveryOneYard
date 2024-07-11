package com.example.VideoChatting.entity;

import com.example.VideoChatting.service.rtc.KurentoUserSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    public HashMap<String, String> userList = new HashMap<String, String>();

    @Transient
    public ConcurrentMap<String, ?> userKurentoList = new ConcurrentHashMap<>();
    private HashMap<String, WebSocketSession> userRtcList = new HashMap<String, WebSocketSession>();
    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public static ChatRoom create(String roomName, String roomPwd, boolean secretCheck, int maxUserCnt){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;
        chatRoom.maxUserCnt = maxUserCnt;
        chatRoom.roomPwd = roomPwd;
        chatRoom.secretCheck = secretCheck;
        return chatRoom;
    }    public ChatRoom createRtc(String roomId, String roomName, String roomPwd, boolean secretCheck, int maxUserCnt){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = roomId;
        chatRoom.roomName = roomName;
        chatRoom.maxUserCnt = maxUserCnt;
        chatRoom.roomPwd = roomPwd;
        chatRoom.secretCheck = secretCheck;
        return chatRoom;
    }

    public void setUserKurentoList(ConcurrentMap<String, ?> userKurentoList) {
        this.userKurentoList = userKurentoList;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}
