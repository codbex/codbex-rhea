package com.codbex.rhea;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AcmeChallengeController {

    @GetMapping("/.well-known/acme-challenge/{token}")
    ResponseEntity<String> handleAcmeChallenge(@PathVariable String token) {
        return ResponseEntity.ok("challenge-token-content");  // Return the challenge content for the token
    }
}
