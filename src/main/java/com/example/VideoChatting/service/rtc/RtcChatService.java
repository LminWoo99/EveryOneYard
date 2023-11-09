
package com.example.VideoChatting.service.rtc;
import com.example.VideoChatting.dto.ChatRoomMap;
import com.example.VideoChatting.dto.KurentoRoomDto;
import com.example.VideoChatting.dto.WebSocketMessage;
import com.example.VideoChatting.entity.ChatRoom;
import com.example.VideoChatting.entity.ChatType;
import com.example.VideoChatting.repository.ChatRoomRepository;
import com.example.VideoChatting.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class RtcChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    public static final String ENTER_INFO = "ENTER_INFO";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;
    private Map<String, ChatRoom> chatRoomMap;
    private final KurentoClient kurento;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }
    public KurentoRoomDto createChatRoom(String roomName, String roomPwd, boolean secretCheck, int maxUserCnt) {
        // roomName 와 roomPwd 로 chatRoom 빌드 후 return
//        ChatRoom room = new ChatRoom().create( roomName, roomPwd, secretCheck
//                , maxUserCnt
//        );
        KurentoRoomDto room = new KurentoRoomDto();
        String roomId = UUID.randomUUID().toString();
        room.setRoomInfo(roomId, roomName, roomPwd, secretCheck, 0, maxUserCnt, ChatType.RTC, kurento);
        room.createPipline();
        // msg 타입이면 ChatType.MSG
        room.setUserKurentoList(new ConcurrentHashMap<String, WebSocketSession>());
        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
        ChatRoom chatRoom = new ChatRoom().createRtc(roomId, roomName, roomPwd, secretCheck, maxUserCnt);
        chatRoom.setChatType(ChatType.RTC);

        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);


        chatRoomRepository.save(chatRoom);
        return room;
    }

    public Map<String, WebSocketSession> getClients(ChatRoom room) {

        Optional<ChatRoom> roomDto = Optional.ofNullable(room);

        return roomDto.get().getUserRtcList();
    }

    public Map<String, WebSocketSession> addClient(ChatRoom room, String name, WebSocketSession session) {
        log.info("room info: []"+ room.getRoomId());
        Map<String, WebSocketSession> userRtcList = room.getUserRtcList();
        log.info("세션 정보"+ session);
        userRtcList.put(name, session);
        return userRtcList;
    }

    // userList 에서 클라이언트 삭제
    public void removeClientByName(ChatRoom room, String userUUID) {
        room.getUserRtcList().remove(userUUID);
    }

    // 유저 카운터 return
    public boolean findUserCount(WebSocketMessage webSocketMessage){
        log.info("웹소켓 데이터 : "+ webSocketMessage.getData());
        ChatRoom room = ChatRoomMap.getInstance().getChatRooms().get(webSocketMessage.getData());

        log.info("room 아이디는 : "+ room.getRoomId());
        log.info("rtc size 22: " + room.getUserRtcList().size());
        return room.getUserRtcList().size() > 1;
    }


}