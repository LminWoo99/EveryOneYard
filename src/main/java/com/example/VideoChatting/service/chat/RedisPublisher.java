package com.example.VideoChatting.service.chat;

import com.example.VideoChatting.dto.ChatDto;
import com.example.VideoChatting.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RedisPublisher {

	private final RedisTemplate<String, Object> redisTemplate;

	public void publish(ChannelTopic topic, ChatDto chatDto) {
		ChatMessage message = chatDto.toEntity();

		redisTemplate.convertAndSend(topic.getTopic(), message);
	}
}