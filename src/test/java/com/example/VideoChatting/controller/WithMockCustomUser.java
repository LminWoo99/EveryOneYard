package com.example.VideoChatting.controller;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory =  WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    // 원하는 필드 추가, 예를 들어:


    String nickname() default "user";

    String email() default "user";

    // ... 기타 속성


}