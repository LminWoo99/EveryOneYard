package com.example.VideoChatting.exception;

public class ChatUserNotFountException extends RuntimeException{
    public ChatUserNotFountException() {
    }

    public ChatUserNotFountException(String message) {
        super(message);
    }

    public ChatUserNotFountException(String message, Throwable cause) {
        super(message, cause);
    }
}
