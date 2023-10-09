package com.example.VideoChatting.service;

import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.exception.ChatRoomNotFoundException;
import com.example.VideoChatting.repository.ChatRoomRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private Map<String, ChatRoom> chatRoomMap;
    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }
    //채팅방
    public List<ChatRoom> findAllRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        Collections.reverse(chatRooms);
        return chatRooms;
    }
    //방 아이디로 검색
    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }

    public ChatRoom createChatRoom(String roomName) {
        if (chatRoomRepository.existsByRoomName(roomName)) {
            throw new ChatRoomNotFoundException("채팅방 이름이 존재합니다");
        } else {
            ChatRoom chatRoom = new ChatRoom().create(roomName);
            chatRoomRepository.save(chatRoom);
            chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
            return chatRoom;
        }

    }

    public void plusUserCnt(String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId);
        if (room != null) {
            room.setUserCount(room.getUserCount() + 1);
            chatRoomRepository.save(room);
        }
    }

    public void minusUserCnt(String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId);
        if (room != null) {
            room.setUserCount(room.getUserCount() - 1);
            chatRoomRepository.save(room);
        }
    }
    public String addUser(String roomId, String username) {
        ChatRoom room = chatRoomMap.get(roomId);
        String userUUID = UUID.randomUUID().toString();
        room.getUserList().put(userUUID, username);

        return userUUID;
    }
    //채팅방 유저 이름 중복 확인
    public String isDuplicateName(String roomId, String username) {
        ChatRoom room = chatRoomMap.get(roomId);
        String temp = username;
        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 네임이라면 다시 랜덤한 숫자 붙이기!
        while(room.getUserList().containsValue(temp)){
            int ranNum = (int) (Math.random()*100)+1;
            temp = username+ranNum;
        }
        return temp;
    }
    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID){
        ChatRoom room = chatRoomMap.get(roomId);
        room.getUserList().remove(userUUID);
    }
    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        ChatRoom room = chatRoomMap.get(roomId);
        return room.getUserList().get(userUUID);
    }

    // 채팅방 전체 userlist 조회
    public ArrayList<String> getUserList(String roomId){
        ArrayList<String> list = new ArrayList<>();

        ChatRoom room = chatRoomMap.get(roomId);

        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 reutrn
        room.getUserList().forEach((key, value) -> list.add(value));
        return list;
    }

}
