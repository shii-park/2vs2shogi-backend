package com.github.com.shii_park.shogi2vs2.controller;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.com.shii_park.shogi2vs2.dto.auth.LoginRequest;
import com.github.com.shii_park.shogi2vs2.dto.auth.LoginResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")//URL
@CrossOrigin(origins = "${app.cors.allowed-origins}")//許可するオリジンは設定ファイルから取得する
public class AuthController {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    public LoginResponse register(@RequestBody @Valid LoginRequest request){
        String userId = UUID.randomUUID().toString(); //ランダムなユーザーID生成

        redisTemplate.opsForValue().set(
            "user:" + userId,
            request.getUsername(),
            Duration.ofHours(2)
        );

        return new LoginResponse(userId, request.getUsername());
    }
}
