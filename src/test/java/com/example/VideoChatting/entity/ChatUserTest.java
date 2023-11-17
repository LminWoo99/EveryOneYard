package com.example.VideoChatting.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChatUserTest {

    @Test
    public void testBuilder() {
        // given
        Long id = 1L;
        String nickname = "testNickname";
        String email = "test@example.com";
        String password = "password";
        Role role = Role.USER;

        // when
        ChatUser user = ChatUser.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .password(password)
                .role(role)
                .build();

        // then
        Assertions.assertNotNull(user);
        Assertions.assertEquals(id, user.getId());
        Assertions.assertEquals(nickname, user.getNickname());
        Assertions.assertEquals(email, user.getEmail());
        Assertions.assertEquals(password, user.getPassword());
        Assertions.assertEquals(role, user.getRole());
    }

    @Test
    public void testUpdate() {
        // given
        ChatUser user = new ChatUser(1L, "nickname", "user@example.com", "password", Role.USER);
        String updatedNickname = "updatedNickname";

        // when
        user.update(updatedNickname);

        // then
        Assertions.assertEquals(updatedNickname, user.getNickname());
    }

    
    @Test
    public void testSetNickname() {
        // given
        ChatUser user = new ChatUser();
        String nickname = "newNickname";

        // when
        user.setNickname(nickname);

        // then
        Assertions.assertEquals(nickname, user.getNickname());
    }

    @Test
    public void testSetEmail() {
        // given
        ChatUser user = new ChatUser();
        String email = "new@example.com";

        // when
        user.setEmail(email);

        // then
        Assertions.assertEquals(email, user.getEmail());
    }
}
