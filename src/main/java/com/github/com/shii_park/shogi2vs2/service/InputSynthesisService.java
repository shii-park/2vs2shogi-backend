package com.github.com.shii_park.shogi2vs2.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.com.shii_park.shogi2vs2.dto.request.MoveRequest;

@Service
public class InputSynthesisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Redisのキー有効期限（ゴミ掃除用）
    private static final Duration REDIS_KEY_TTL = Duration.ofSeconds(32);

    public List<MoveRequest> handleInput(String gameId, String teamId, MoveRequest input) {
        // キーの構築
        String key = "game:" + gameId + ":team:" + teamId + ":moves";

        try {
            // 既存リストの取得
            List<String> existingJsonList = redisTemplate.opsForList().range(key, 0, -1);

            // 二重送信チェック 
            if (existingJsonList != null) {
                for (String json : existingJsonList) {
                    MoveRequest req = objectMapper.readValue(json, MoveRequest.class);
                    if (req.getuserId().equals(input.getuserId())) {
                        System.out.println("既に入力済みのユーザです: " + input.getuserId());
                        return null; 
                    }
                }
            }

            // 3. 今回の入力を保存
            String jsonInput = objectMapper.writeValueAsString(input);
            Long currentSize = redisTemplate.opsForList().rightPush(key, jsonInput);

            // 最初の1人の場合、TTLセットして待機
            if (currentSize != null && currentSize == 1) {
                redisTemplate.expire(key, REDIS_KEY_TTL);
                return null;
            }

            // 4. 2人揃った場合
            if (currentSize != null && currentSize >= 2) {
                List<String> allMovesJson = redisTemplate.opsForList().range(key, 0, -1);
                redisTemplate.delete(key);

                List<MoveRequest> result = new ArrayList<>();
                if (allMovesJson != null) {
                    for (String json : allMovesJson) {
                        result.add(objectMapper.readValue(json, MoveRequest.class));
                    }
                }
                return result;
            }

            return null;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("JSON変換エラー", e);
        }
    }
}