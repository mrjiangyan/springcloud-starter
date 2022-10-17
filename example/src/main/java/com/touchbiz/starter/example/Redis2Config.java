package com.touchbiz.starter.example;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis2")
public class Redis2Config {

    private int database;

    private String host;

    private int port;

    private String password;

    private int timeout;
}
