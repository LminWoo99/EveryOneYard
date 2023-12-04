//package com.example.VideoChatting.service.chat;
//
//import com.example.VideoChatting.entity.ChatRoom;
//import com.example.VideoChatting.repository.ChatMessageRepository;
//import com.example.VideoChatting.repository.ChatRoomRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//
//import java.util.List;
//
//@SpringBootTest
//public class ChatRoomTest {
//    @Autowired
//    ChatRoomService chatRoomService;
//    @Autowired
//    ChatMessageRepository chatMessageRepository;
//    @Autowired
//    ChatRoomRepository chatRoomRepository;
//
//    @Test
//    @DisplayName("최적화 전 채팅방 전체 조회")
//    void findAllChatRoom() throws Exception{
//        //given
//        List<ChatRoom> chatRooms = chatRoomService.findAllRooms();
//        chatMessageRepository.findAll();
//
//        //then
//        Assertions.assertThat(chatRooms.size()).isEqualTo(100000);
//
//    }
//    @Test
//    @DisplayName("EntityGraph 최적화  채팅방 전체 조회")
//    void findAllChatRoom_V1() throws Exception{
//        //given
//        long startTime = System.currentTimeMillis();
//        List<ChatRoom> chatRooms = chatRoomService.findAllRooms();
//        //then
//        Assertions.assertThat(chatRooms.size()).isEqualTo(100000);
//        long endTime = System.currentTimeMillis();
//
//        System.out.println("Execution Time: " + (endTime - startTime) + " ms");
//
//    }
//    @Test
//    @DisplayName("페이징 최적화  채팅방 전체 조회")
//    void findAllChatRoom_V2() throws Exception{
//        //given
//        Pageable pageable = PageRequest.of(0, 10);
//        long startTime = System.currentTimeMillis();
//        Page<ChatRoom> chatRooms = chatRoomRepository.findAll(pageable);
//        //then
////        Assertions.assertThat(chatRooms.size()).isEqualTo(100000);
//        long endTime = System.currentTimeMillis();
//
//        System.out.println("Execution Time: " + (endTime - startTime) + " ms");
//
//    }
//    @Test
//    @DisplayName("Slice 최적화  채팅방 전체 조회")
//    void findAllChatRoom_V3() throws Exception{
//        //given
//        Pageable pageable = PageRequest.of(0, 10);
//        long startTime = System.currentTimeMillis();
//        Slice<ChatRoom> chatRooms = chatRoomRepository.findAllChatRooms(pageable);
//
//        //then
////        Assertions.assertThat(chatRooms.size()).isEqualTo(100000);
//        long endTime = System.currentTimeMillis();
//
//        System.out.println("Execution Time: " + (endTime - startTime) + " ms");
//
//    }
//
//}
