package com.example.VideoChatting.dto;

import com.example.VideoChatting.entity.ChatRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// 싱글톤으로 생성
// 모든 ChatService 에서 ChatRooms가 공통된 필요함으로
@Getter
@Setter
public class ChatRoomMap {
    private static ChatRoomMap chatRoomMap = new ChatRoomMap();
    private ConcurrentMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private ChatRoomMap(){}

    public static ChatRoomMap getInstance(){
        return chatRoomMap;
    }

}
