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

@RestController
@RequestMapping("/api/match")
@CrossOrigin(origins = "*")
public class MatchMakingController {
    @Autowired
    private MatchingService matchingService;

    @PostMapping("/join")
    public GameStatusResponse joinMatch(@RequestParam String userId){
        matchingService.joinQueue(userId);
        return new GameStatusResponse("WAITING", null);
    }

    @GetMapping("/status")
    public GameStatusResponse checkStatus(@RequestParam String userId){
        return matchingService.checkStatus(userId);
    }
}
