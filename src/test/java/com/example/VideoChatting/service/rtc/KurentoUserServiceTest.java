package com.example.VideoChatting.service.rtc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class KurentoUserServiceTest {
    @InjectMocks
    private KurentoUserService service;
    @Mock
    private WebSocketSession session;

    @Mock
    private KurentoUserSession userSession;


    @BeforeEach
    void setUp() {
        when(session.getId()).thenReturn("session_id");
        when(userSession.getSession()).thenReturn(session);
        when(userSession.getName()).thenReturn("이민우");
    }

    @Test
    @DisplayName("userSession 을 파라미터로 받은 후 해당 객체에서 userName 과 sessionId 를 key 로해서 userSession 저장 테스트")
    void registerTest() {
        //when
        service.register(userSession);
        //then
        assertThat(userSession).isEqualTo(service.getBySession(session));
        assertThat(userSession).isEqualTo(service.getByName("이민우"));

    }

    @Test
    @DisplayName("유저명으로 UserSession 찾는 테스트")
    void getByNameTest() {
        //when
        service.register(userSession);
        //then
        assertThat(userSession).isEqualTo(service.getByName("이민우"));
    }

    @Test
    @DisplayName("유저 세션 찾는 테스트")
    void getBySessionTest() {
        service.register(userSession);
        assertThat(userSession).isEqualTo(service.getBySession(session));
    }

    @Test
    @DisplayName("해당 유저 존재하는지 확인하는 테스트")
    void existsTest() {
        assertFalse(service.exists("이민우"));
        service.register(userSession);
        assertThat(Boolean.TRUE).isEqualTo(service.exists("이민우"));
    }

    @Test
    @DisplayName("세션 삭제 테스트")
    void removeBySessionTest() {
        service.register(userSession);
        assertThat(userSession).isEqualTo(service.getBySession(session));
        service.removeBySession(session);
        assertNull(service.getBySession(session));
        assertFalse(service.exists("이민우"));
    }
}
