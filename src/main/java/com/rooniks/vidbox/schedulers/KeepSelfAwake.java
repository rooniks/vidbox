package com.rooniks.vidbox.schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepSelfAwake {
    private static final Logger logger = LoggerFactory.getLogger(KeepSelfAwake.class);

    @Value("${self.awake.url}")
    String url;

    @Scheduled(fixedDelayString = "${self.awake.frequency}")
    public void checkSelfHealth() {
        logger.info("Hitting self health check..");
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        logger.info("Result: {}", result);
    }
}
