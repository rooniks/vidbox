package com.rooniks.vidbox.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {
    @GetMapping("/health_check")
    public Map<String, String> checkHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("message", "VidBox is up and running");
        return health;
    }
}
