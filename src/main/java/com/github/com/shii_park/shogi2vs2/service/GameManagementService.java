package com.github.com.shii_park.shogi2vs2.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class GameManagementService {

    // 発行したゲームIDと、参加予定のユーザーIDリストを一時保存するマップ
    // key: gameId, value: List<userId>
    private final Map<String, List<String>> pendingGames = new ConcurrentHashMap<>();

    /**
     * プレイヤー4人のためにゲームIDを発行し、予約リストに登録する
     * (MatchingServiceから呼ばれる)
     */
    public String createGameForPlayers(List<String> userIds) {
        String gameId = "game-" + UUID.randomUUID().toString();
        
        // 予約情報を保存
        pendingGames.put(gameId, userIds);
        
        System.out.println("Game reserved: " + gameId + " for players: " + userIds);
        return gameId;
    }

    /**
     * 接続してきたユーザーが、そのゲームの正当な参加者か確認する
     */
    public boolean isValidPlayer(String gameId, String userId) {
        List<String> players = pendingGames.get(gameId);
        return players != null && players.contains(userId);
    }
    
    /**
     * ゲーム開始後に予約情報を消す（メモリ節約）
     */
    public void removePendingGame(String gameId) {
        pendingGames.remove(gameId);
    }
    
    public List<String> getReservedPlayers(String gameId) {
        return pendingGames.get(gameId);
    }
}