package com.github.com.shii_park.shogi2vs2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.com.shii_park.shogi2vs2.model.domain.action.GameAction;

/**
 * チーム内の複数プレイヤーからの入力を合成するサービス
 * 各チームのプレイヤーからのアクション入力を受け付け、必要な人数が揃ったら取得する
 */
@Service
public class InputSynthesisService {

    /**
     * Redisテンプレート
     * アクション入力の一時保存に使用
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * JSONシリアライザ
     * GameActionオブジェクトとJSON文字列の相互変換に使用
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * アクションを受け付け、Redisに保存する。
     * 同じユーザーからの二重送信を弾くロジックを追加
     */
    public List<GameAction> handleActionInput(String gameId, String teamId, GameAction action) {
        String inputKey = getKey(gameId, teamId);
        String votersKey = inputKey + ":voters"; // 「誰が投票したか」を管理するキー

        try {
            // 1. 【二重送信チェック】
            // SetにユーザーIDを追加してみる。
            // add は「新しく追加できたら 1」「既にあったら 0」を返す。
            Long added = redisTemplate.opsForSet().add(votersKey, action.getUserId());

            if (added == null || added == 0) {
                System.out.println("Duplicate action ignored: " + action.getUserId());
                return null;
            }

            // 2. まだ送信していない人なら、リストに追加
            String json = objectMapper.writeValueAsString(action);
            redisTemplate.opsForList().rightPush(inputKey, json);

            // 3. 2人分揃ったか確認
            Long size = redisTemplate.opsForList().size(inputKey);

            if (size != null && size >= 2) {
                return retrieveAndClear(inputKey, votersKey);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Timeoutしたとき入力がそろってなくても強制回収
     */
    public List<GameAction> forceRetrieveInputs(String gameId, String teamId) {
        String inputKey = getKey(gameId, teamId);
        String votersKey = inputKey + ":voters";
        return retrieveAndClear(inputKey, votersKey);
    }

    // --- 内部ヘルパー ---

    /**
     * Redisからアクション入力を取得し、データを削除する
     * 
     * @param inputKey アクションリストのRedisキー
     * @param votersKey 投票者セットのRedisキー
     * @return 取得したアクションのリスト
     */
    private List<GameAction> retrieveAndClear(String inputKey, String votersKey) {
        // データを取得
        List<String> jsonList = redisTemplate.opsForList().range(inputKey, 0, -1);
        
        //  入力データだけでなく投票者リストも消す
        redisTemplate.delete(inputKey);
        redisTemplate.delete(votersKey);

        List<GameAction> result = new ArrayList<>();
        if (jsonList == null) return result;

        for (String json : jsonList) {
            try {
                GameAction action = objectMapper.readValue(json, GameAction.class);
                result.add(action);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * ゲームIDとチームIDからRedisキーを生成する
     * 
     * @param gameId ゲームID
     * @param teamId チームID
     * @return 生成されたRedisキー
     */
    private String getKey(String gameId, String teamId) {
        return "game:" + gameId + ":team:" + teamId + ":inputs";
    }
}