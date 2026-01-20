package com.github.com.shii_park.shogi2vs2.dto.auth;

/**
 * ログインレスポンスDTO
 * ログイン成功時にクライアントに返されるレスポンス情報を保持します。
 * 
 * @param sessionId セッションID
 * @param username ユーザー名
 */
//レコードクラスなのでこれで必要なものが全て実装できている
public record LoginResponse(String sessionId, String username) {
}
