package com.github.com.shii_park.shogi2vs2.controller;

import com.github.com.shii_park.shogi2vs2.dto.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")//URL
@CrossOrigin(origins = "*")//Next.jaからのアクセスを許可
public class AuthController {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        String userID = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
            "user:" + userID,
            request.getUsername(),
            Duration.ofHours(2)
        );

        return new LoginResponse(userId, request.getUsername());
    }
}
