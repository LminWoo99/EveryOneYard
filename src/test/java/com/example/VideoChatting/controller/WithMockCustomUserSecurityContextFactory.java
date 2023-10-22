package com.example.VideoChatting.controller;

import com.example.VideoChatting.entity.ChatUser;
import com.example.VideoChatting.entity.SessionUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        ChatUser chatUser = new ChatUser();
        chatUser.setEmail(annotation.email());
        chatUser.setNickname(annotation.nickname());
        // chatUser에 다른 필드 설정도 필요한 경우 여기서 설정하세요.

        SessionUser sessionUser = new SessionUser(chatUser);

        Authentication authentication =new UsernamePasswordAuthenticationToken(sessionUser, null, sessionUser.getAuthorities());

        securityContext.setAuthentication(authentication);
        System.out.println("authentication.getAuthorities() = " + authentication.getAuthorities());
        return securityContext;
    }

}