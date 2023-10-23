package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.entity.ChatMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.exception.ChatRoomNotFoundException;
import com.example.VideoChatting.repository.ChatRoomRepository;
import com.example.VideoChatting.service.file.FileService;
import com.example.VideoChatting.service.file.S3FileService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    public static final String ENTER_INFO = "ENTER_INFO";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        hashOpsEnterInfo=redisTemplate.opsForHash();
        topics = new HashMap<>();
    }
    //채팅방
    //방 아이디로 검색
//    @Cacheable(value = "ChatRoom", cacheManager = "testCacheManager")
    public List<ChatRoom> findAllRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        Collections.reverse(chatRooms);

//        return opsHashChatRoom.values(CHAT_ROOMS);
        return chatRooms;
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }
    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
    public ChatRoom createChatRoom(String roomName,String roomPwd, boolean secretCheck, int maxUserCnt) {
        if (chatRoomRepository.existsByRoomName(roomName)) {
            throw new ChatRoomNotFoundException("채팅방 이름이 존재합니다");
        } else {
            ChatRoom chatRoom = new ChatRoom().create(roomName, roomPwd, secretCheck, maxUserCnt);
            opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
            log.info(chatRoom.getRoomName());
            chatRoomRepository.save(chatRoom);
            return chatRoom;
        }

    }

    public void createChatMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        chatRoom.addChatMessages(chatMessage);
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
            log.info(String.valueOf(room.getUserCount()));
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
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        String userUUID = UUID.randomUUID().toString();
        room.getUserList().put(userUUID, username);
        return userUUID;
    }
    //채팅방 유저 이름 중복 확인
    public String isDuplicateName(String roomId, String username) {
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS,roomId);
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
        ChatRoom room = opsHashChatRoom.get(ENTER_INFO, roomId);
        room.getUserList().remove(userUUID);
    }
    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        ChatRoom room = opsHashChatRoom.get(ENTER_INFO, roomId);
        return room.getUserList().get(userUUID);
    }

    // 채팅방 전체 userlist 조회
    public ArrayList<String> getUserList(String roomId){
        ArrayList<String> list = new ArrayList<>();

        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);

        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 reutrn
        room.getUserList().forEach((key, value) -> list.add(value));
        return list;
    }
    // 채팅방 최대 인원 체크
    public boolean checkRoomUserCnt(String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId);

//        log.info(String.valueOf(room.getUserCount()));
//        log.info("참여인원 확인 [{}, {}]", room.getUserCount(), room.getMaxUserCnt());

        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
            return false;
        }
        return true;
    }
    //비밀번호 조회
    public boolean confirmPwd(String roomId, String roomPwd) {
        return roomPwd.equals(opsHashChatRoom.get(CHAT_ROOMS, roomId).getRoomPwd());
    }
    @CacheEvict(value = "ChatRoom", key = "#roomId", cacheManager = "testCacheManager")
    public void deleteChatRoom(String roomId) {
        try {
            opsHashChatRoom.delete(roomId);
            ChatRoom byRoomId = chatRoomRepository.findByRoomId(roomId);
            chatRoomRepository.delete(byRoomId);
            fileService.deleteFileDir(roomId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
