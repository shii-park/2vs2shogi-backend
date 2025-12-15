package com.github.com.shii_park.shogi2vs2.dto.auth;

import lombok.Data;
@Data
// @AllArgsConstructor
public class LoginResponse {
    private String userId;     //ユーザーID
    private String username;   //ユーザー名

    public LoginResponse(String userId,String username){
        this.userId = userId;
        this.username = username;
    }
}

