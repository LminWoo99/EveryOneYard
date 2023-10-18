package com.example.VideoChatting.entity;


import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class SessionUser implements OAuth2User, Serializable {
    private ChatUser chatUser;

    public SessionUser(ChatUser chatUser) {
        this.chatUser = chatUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        // 예제: ChatUser 객체에서 필요한 속성을 추가
        attributes.put("nickname", chatUser.getNickname());
        attributes.put("email", chatUser.getEmail());
        // 필요한 다른 속성들도 여기에 추가 가능
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collections = new ArrayList<>();

        collections.add(() -> {
            return "ROLE_" + chatUser.getRole();
        });

        return collections;
    }


    @Override
    public String getName() {
        return chatUser.getNickname();
    }
}