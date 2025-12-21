package com.github.com.shii_park.shogi2vs2.dto.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * LoginResponseのテスト
 * レコードクラスの基本機能を確認
 */
class LoginResponseTest {

    /**
     * LoginResponseの生成と基本プロパティの確認
     */
    @Test
    void testLoginResponseCreation() {
        String sessionId = "session-123";
        String username = "testuser";

        LoginResponse response = new LoginResponse(sessionId, username);

        assertEquals(sessionId, response.sessionId());
        assertEquals(username, response.username());
    }

    /**
     * レコードの等価性確認
     */
    @Test
    void testLoginResponseEquality() {
        LoginResponse response1 = new LoginResponse("session-1", "user1");
        LoginResponse response2 = new LoginResponse("session-1", "user1");
        LoginResponse response3 = new LoginResponse("session-2", "user1");

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
    }

    /**
     * toStringメソッドの確認
     */
    @Test
    void testLoginResponseToString() {
        LoginResponse response = new LoginResponse("session-abc", "myuser");
        String str = response.toString();

        assertTrue(str.contains("session-abc"));
        assertTrue(str.contains("myuser"));
    }

    /**
     * hashCodeの一貫性確認
     */
    @Test
    void testLoginResponseHashCode() {
        LoginResponse response1 = new LoginResponse("session-1", "user1");
        LoginResponse response2 = new LoginResponse("session-1", "user1");

        assertEquals(response1.hashCode(), response2.hashCode());
    }

    /**
     * nullパラメータでの生成確認
     */
    @Test
    void testLoginResponseWithNullValues() {
        LoginResponse response = new LoginResponse(null, null);

        assertNull(response.sessionId());
        assertNull(response.username());
    }

    /**
     * 空文字列での生成確認
     */
    @Test
    void testLoginResponseWithEmptyStrings() {
        LoginResponse response = new LoginResponse("", "");

        assertEquals("", response.sessionId());
        assertEquals("", response.username());
    }

    /**
     * UUID形式のsessionIdの確認
     */
    @Test
    void testLoginResponseWithUUID() {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        LoginResponse response = new LoginResponse(uuid, "user");

        assertEquals(uuid, response.sessionId());
    }

    /**
     * 日本語ユーザー名の確認
     */
    @Test
    void testLoginResponseWithJapaneseUsername() {
        LoginResponse response = new LoginResponse("session-1", "太郎");

        assertEquals("太郎", response.username());
    }

    /**
     * 長いセッションIDの確認
     */
    @Test
    void testLoginResponseWithLongSessionId() {
        String longSessionId = "a".repeat(100);
        LoginResponse response = new LoginResponse(longSessionId, "user");

        assertEquals(longSessionId, response.sessionId());
        assertEquals(100, response.sessionId().length());
    }
}
