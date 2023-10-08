package com.example.VideoChatting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserDto {
    private Long id;
    private String nickname;
    private String email;
    private String provider;


}
