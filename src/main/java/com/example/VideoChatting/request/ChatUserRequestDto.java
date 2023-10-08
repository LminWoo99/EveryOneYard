package com.example.VideoChatting.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserRequestDto {
    private String nickname;
    private String email;
    private String provider;
}
