package com.example.VideoChatting.service.oAuth;

import com.example.VideoChatting.entity.ChatUser;
import com.example.VideoChatting.exception.ChatUserNotFountException;
import com.example.VideoChatting.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatUserService {
    private final ChatUserRepository chatUserRepository;

    public Optional<ChatUser> findUserByEmail(String email) {
        Optional<ChatUser> chatUser = chatUserRepository.findByEmail(email);
        if (chatUser==null){
            throw new ChatUserNotFountException("해당 유저가 존재하지 않습니다");

        }
        else{
            return chatUser;
        }
    }
}
