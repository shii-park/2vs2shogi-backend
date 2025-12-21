package com.github.com.shii_park.shogi2vs2.dto.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * LoginRequestのバリデーションテスト
 * ユーザー名の入力検証を確認
 */
class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 正常なユーザー名の確認
     */
    @Test
    void testValidUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    /**
     * 最小文字数（1文字）の確認
     */
    @Test
    void testMinimumLengthUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("a");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    /**
     * 最大文字数（20文字）の確認
     */
    @Test
    void testMaximumLengthUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("12345678901234567890"); // 20文字

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    /**
     * 空文字列の場合エラーになることを確認
     */
    @Test
    void testEmptyUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("必須"));
    }

    /**
     * nullの場合エラーになることを確認
     */
    @Test
    void testNullUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername(null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    /**
     * 空白文字のみの場合エラーになることを確認
     */
    @Test
    void testBlankUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("   ");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    /**
     * 最大文字数を超えた場合エラーになることを確認
     */
    @Test
    void testTooLongUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("123456789012345678901"); // 21文字

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("20文字以内"));
    }

    /**
     * 日本語ユーザー名の確認
     */
    @Test
    void testJapaneseUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("テストユーザー");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    /**
     * 特殊文字を含むユーザー名の確認
     */
    @Test
    void testUsernameWithSpecialCharacters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user_123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    /**
     * getterとsetterの動作確認
     */
    @Test
    void testGetterSetter() {
        LoginRequest request = new LoginRequest();
        String username = "myusername";
        
        request.setUsername(username);
        assertEquals(username, request.getUsername());
    }
}
