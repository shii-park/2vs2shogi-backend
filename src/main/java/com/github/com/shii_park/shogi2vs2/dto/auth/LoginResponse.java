package com.github.com.shii_park.shogi2vs2.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class LoginResponse {
    private String userID;
    private String username;
}

