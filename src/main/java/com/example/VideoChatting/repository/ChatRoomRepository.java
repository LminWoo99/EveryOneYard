package com.example.VideoChatting.repository;

import com.example.VideoChatting.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByRoomId(String roomId);

    boolean existsByRoomName(String roomName);

    @Modifying
    @Query("update ChatRoom t set t.roomName = :roomName where t.roomId = :roomId")
    void updateRoomName(@Param("roomId") String roomId, @Param("roomName") String roomName);
    @Modifying
    @Query("update ChatRoom t set t.roomPwd = :roomPwd where t.roomId = :roomId")
    void updateRoomPwd(@Param("roomId") String roomId, @Param("roomPwd") String roomPwd);
    @Modifying
    @Query("update ChatRoom t set t.secretCheck = :secretCheck where t.roomId = :roomId")
    void updateRoomSecretCheck(@Param("roomId")String roomId, @Param("secretCheck")Boolean secretCheck);

////    @Override
////    @EntityGraph(attributePaths = {"chatMessageList"})
////    List<ChatRoom> findAll();
//    @Override
//    @Query("SELECT b FROM ChatRoom b order by b.id desc")
//    @EntityGraph(attributePaths = {"chatMessageList"})
//    Page<ChatRoom> findAll(Pageable pageable);
//    @Query("SELECT b FROM ChatRoom b order by b.id desc")
////    @EntityGraph(attributePaths = {"chatMessageList"})
//    Slice<ChatRoom> findAllChatRooms(Pageable pageable);
}
