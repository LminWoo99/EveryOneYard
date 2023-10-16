package com.example.VideoChatting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
// Stomp 를 통해 pub/sub 를 사용하면 구독자 관리가 알아서 된다!!
// 따라서 따로 세션 관리를 하는 코드를 작성할 필도 없고,
// 메시지를 다른 세션의 클라이언트에게 발송하는 것도 구현 필요가 없다!
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

    public ChatRoom create(String roomName){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;
        chatRoom.maxUserCnt = maxUserCnt;
        chatRoom.roomPwd = roomPwd;
        return chatRoom;
    }


    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
    public void addChatMessages(ChatMessage chatMessage) {
        this.chatMessageList.add(chatMessage);
    }
}
