package com.github.com.shii_park.shogi2vs2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.github.com.shii_park.shogi2vs2.handler.GameWebSocketHandler;

/**
 * WebSocket通信の設定クラス
 * ゲームのリアルタイム通信を実現するためのWebSocketエンドポイントを設定する
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private GameWebSocketHandler gameWebSocketHandler;

    /**
     * WebSocketハンドラーを登録する
     * 
     * @param registry WebSocketハンドラーレジストリ
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // ゲーム用WebSocketエンドポイント(/ws/game)を登録
        registry.addHandler(gameWebSocketHandler, "/ws/game")
                .setAllowedOrigins("*");// フロントのURLを指定
    }
    
}
