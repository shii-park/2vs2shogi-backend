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

/**
 * 認証機能を提供するコントローラー
 * ユーザー登録などの認証関連のエンドポイントを管理する
 */
@RestController
@RequestMapping("/api/auth") // URL
@CrossOrigin(origins = "*") // TODO: 許可するオリジンを環境変数から取得
public class AuthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * ユーザー登録処理
     * ユーザー名を受け取り、新しいユーザーIDを生成してRedisに保存する
     * 
     * @param request ユーザー名を含むログインリクエスト
     * @return 生成されたユーザーIDとユーザー名を含むレスポンス
     */
    @PostMapping("/register")
    public LoginResponse register(@RequestBody @Valid LoginRequest request) {
        System.out.println("[AuthController] 登録リクエストを受信しました。ユーザー名=" + request.getUsername());
        
        // ランダムなユーザーID生成
        String userId = UUID.randomUUID().toString();
        System.out.println("[AuthController] ユーザーIDを生成しました。ユーザーID=" + userId);

        // Redisにユーザー情報を保存(有効期限: 2時間)
        redisTemplate.opsForValue().set(
                "user:" + userId,
                request.getUsername(),
                Duration.ofHours(2));
        System.out.println("[AuthController] ユーザー情報を保存しました。キー=user:" + userId + ", TTL=2時間");

        return new LoginResponse(userId, request.getUsername());
    }
}
