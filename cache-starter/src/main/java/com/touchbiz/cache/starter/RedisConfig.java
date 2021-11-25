package com.touchbiz.cache.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
class RedisConfig {

    private int database;

    private String host;

    private int port;

    private String password;

    private int timeout;

    private int timeBetweenEvictionRuns;

}