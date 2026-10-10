package com.gymtracker.gymtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Bratislava");

    @Bean
    public Clock clock() {
        return Clock.system(APP_ZONE);
    }
}