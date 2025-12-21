package com.github.com.shii_park.shogi2vs2.dto.auth;

//レコードクラスなのでこれで必要なものが全て実装できている
public record LoginResponse(String sessionId, String username) {
}
