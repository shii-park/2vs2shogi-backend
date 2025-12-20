package com.github.com.shii_park.shogi2vs2.controller;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.com.shii_park.shogi2vs2.dto.auth.LoginRequest;
import com.github.com.shii_park.shogi2vs2.dto.auth.LoginResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth") // URL
@CrossOrigin(origins = "*") // 許可するオリジンは設定ファイルから取得する
public class AuthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    public LoginResponse register(@RequestBody @Valid LoginRequest request) {
        System.out.println("[AuthController] 登録リクエストを受信しました。ユーザー名=" + request.getUsername());
        String userId = UUID.randomUUID().toString(); // ランダムなユーザーID生成
        System.out.println("[AuthController] ユーザーIDを生成しました。ユーザーID=" + userId);

        redisTemplate.opsForValue().set(
                "user:" + userId,
                request.getUsername(),
                Duration.ofHours(2));
        System.out.println("[AuthController] ユーザー情報を保存しました。キー=user:" + userId + ", TTL=2時間");

        return new LoginResponse(userId, request.getUsername());
    }
}
