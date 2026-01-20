package com.github.com.shii_park.shogi2vs2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 2vs2将棋アプリケーションのメインクラス
 * Spring Bootアプリケーションのエントリーポイント
 */
@SpringBootApplication
public class Shogi2vs2Application {

	/**
	 * アプリケーションのメインメソッド
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		SpringApplication.run(Shogi2vs2Application.class, args);
	}

}
