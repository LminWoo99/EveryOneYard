package com.example.VideoChatting.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String email;
    private String password;
    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public ChatUser(Long id, String nickname, String email, String password, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

//    public static ChatUser createChatUser(String nickname, String email, String provider) {
//        ChatUser chatUser = new ChatUser();
//        chatUser.nickname = nickname;
//        chatUser.email = email;
//        chatUser.provider = provider;
//        return chatUser;
//
//
//    }

    public ChatUser update(String nickname) {
        this.nickname = nickname;
        return this;
    }
    public String getRoleKey() {
        return this.role.getKey();
}
}
