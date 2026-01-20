package com.github.com.shii_park.shogi2vs2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.com.shii_park.shogi2vs2.dto.response.GameStatusResponse;
import com.github.com.shii_park.shogi2vs2.service.MatchingService;

/**
 * マッチメイキング機能を提供するコントローラー
 * プレイヤーのキュー参加とマッチング状態の確認を管理する
 */
@RestController
@RequestMapping("/api/match")
@CrossOrigin(origins = "*")
public class MatchMakingController {
    @Autowired
    private MatchingService matchingService;

    /**
     * マッチングキューに参加する
     * プレイヤーをマッチング待機列に追加する
     * 
     * @param userId 参加するユーザーID
     * @return マッチング状態のレスポンス(WAITING状態)
     */
    @PostMapping("/join")
    public GameStatusResponse joinMatch(@RequestParam String userId){
        matchingService.joinQueue(userId);
        return new GameStatusResponse("WAITING", null);
    }

    /**
     * マッチング状態を確認する
     * ユーザーの現在のマッチング状態やゲームIDを取得する
     * 
     * @param userId 確認するユーザーID
     * @return マッチング状態とゲームID(マッチング成立時)を含むレスポンス
     */
    @GetMapping("/status")
    public GameStatusResponse checkStatus(@RequestParam String userId){
        return matchingService.checkStatus(userId);
    }
}
