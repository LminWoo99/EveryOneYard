package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.ChatType;
import com.example.VideoChatting.exception.ChatRoomNotFoundException;
import com.example.VideoChatting.repository.ChatRoomRepository;
import com.example.VideoChatting.service.file.FileService;
import com.example.VideoChatting.service.file.S3FileService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    private final S3FileService fileService;

    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;
    @PostConstruct
    private void initTopics() {
        this.topics = new ConcurrentHashMap<>();
    }

    //채팅방
    //방 아이디로 검색
    public List<ChatRoom> findAllRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        Collections.reverse(chatRooms);

        return chatRooms;
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }
    /**
     * 채팅방 생성
     */
    public ChatRoom createChatRoom(String roomName,String roomPwd, boolean secretCheck, int maxUserCnt) {
        if (chatRoomRepository.existsByRoomName(roomName)) {
            throw new ChatRoomNotFoundException("채팅방 이름이 존재합니다");
        } else {
            ChatRoom chatRoom = new ChatRoom().create(roomName, roomPwd, secretCheck, maxUserCnt);
            chatRoom.setChatType(ChatType.MSG);
            chatRoomRepository.save(chatRoom);
            return chatRoom;
        }

    }
    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null)
            topic = new ChannelTopic(roomId);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(roomId, topic);
    }
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
    public void plusUserCnt(String roomId) {

        ChatRoom room = chatRoomRepository.findByRoomId(roomId);
        log.info(String.valueOf(room.getUserCount()));
        if (room != null) {
            room.setUserCount(room.getUserCount() + 1);

            chatRoomRepository.save(room);
        }
    }

    public void minusUserCnt(String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId);
        if (room != null && room.getUserCount()!=0) {
            room.setUserCount(room.getUserCount() - 1);
            chatRoomRepository.save(room);
        } else if (room.getUserCount()==0) {
            room.setUserCount(0);
            chatRoomRepository.save(room);
        }
    }
    public String addUser(String roomId, String username) {
        String userUUID = UUID.randomUUID().toString();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        chatRoom.getUserList().put(userUUID, username);

        return userUUID;
    }
    //채팅방 유저 이름 중복 확인
    public String isDuplicateName(String roomId, String username) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        String temp = username;
        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 네임이라면 다시 랜덤한 숫자 붙이기!
        temp = randomUsername(username, chatRoom, temp);
        return temp;
    }

    private static String randomUsername(String username, ChatRoom chatRoom, String temp) {
        while(chatRoom.getUserList().containsValue(temp)){
            int ranNum = (int) (Math.random()*100)+1;
            temp = username +ranNum;
        }
        return temp;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID){
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        chatRoom.getUserList().remove(userUUID);
    }
    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        return chatRoom.getUserList().get(userUUID);
    }

    // 채팅방 전체 userlist 조회
    public List<String> getUserList(String roomId){
        List<String> list = new ArrayList<>();

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 return
        chatRoom.getUserList().forEach((key, value) -> list.add(value));
        return list;
    }
    // 채팅방 최대 인원 체크
    public boolean checkRoomUserCnt(String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId);

        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
            return false;
        }
        return true;
    }
    //비밀번호 조회
    public boolean confirmPwd(String roomId, String roomPwd) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        return chatRoom.getRoomPwd().equals(roomPwd);
    }
    public void deleteChatRoom(String roomId) {
        try {
            chatRoomRepository.deleteByRoomId(roomId);
            fileService.deleteFileDir(roomId);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    public void updateRoomName(String roomId, String roomName) {
        chatRoomRepository.updateRoomName(roomId, roomName);

    }
    public void updateRoomPassWord(String roomId, String roomPwd) {
        chatRoomRepository.updateRoomPwd(roomId, roomPwd);
    }

    public void updateRoomSecretCheck(String roomId, String secretCheck) {
        log.info(secretCheck);
        chatRoomRepository.updateRoomSecretCheck(roomId, Boolean.valueOf(secretCheck));
    }
}