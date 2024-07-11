package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedisPublisherTest {
    @InjectMocks
    RedisPublisher redisPublisher;
    @Mock
    private RedisTemplate<String, ChatDto> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisPublisher, "redisTemplate", redisTemplate);
    }

    @Test
    public void testPublish() {
        // given
        ChatDto chatDto = new ChatDto();

        // when
        redisPublisher.publish(channelTopic, chatDto);

        // then
        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), chatDto);
    }
}