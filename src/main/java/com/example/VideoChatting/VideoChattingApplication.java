package com.example.VideoChatting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing

public class VideoChattingApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoChattingApplication.class, args);
	}

}
