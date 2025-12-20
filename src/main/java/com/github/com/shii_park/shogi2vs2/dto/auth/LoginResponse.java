package com.github.com.shii_park.shogi2vs2.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String sessionId; // ユーザーID
    private String username; // ユーザー名

    public LoginResponse(String userId, String username) {
        this.sessionId = userId;
        this.username = username;
    }
}