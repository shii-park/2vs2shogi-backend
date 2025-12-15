package com.github.com.shii_park.shogi2vs2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Shogi2vs2Application {

	public static void main(String[] args) {
		SpringApplication.run(Shogi2vs2Application.class, args);
	}

}
