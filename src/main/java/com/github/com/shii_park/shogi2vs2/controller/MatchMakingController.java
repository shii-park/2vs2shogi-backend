package com.github.com.shii_park.shogi2vs2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.com.shii_park.shogi2vs2.dto.response.MatchStatusResponse;
import com.github.com.shii_park.shogi2vs2.service.MatchingService;

@RestController
@RequestMapping("/api/match")
@CrossOrigin(origins = "*")
public class MatchMakingController {
    @Autowired
    private MatchingService matchingService;

    @PostMapping("/join")
    public MatchStatusResponse joinMatch(@RequestParam String userId){
        matchingService.joinQueue(userId);
        return new MatchStatusResponse("WAITING", null);
    }

    @GetMapping("/status")
    public MatchStatusResponse checkStatus(@RequestParam String userId){
        return matchingService.checkStatus(userId);
    }
}
