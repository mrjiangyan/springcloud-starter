package com.touchbiz.cache.starter.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "enable", havingValue = "true")
@ConditionalOnExpression(value = "'${spring.cache.type}'== 'caffeine'|| '${spring.cache.type}'=='caffeine-redis'")
public class CaffeineConfig {

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        System.out.println("caffeineCacheManager被创建");
        return new CaffeineCacheManager();
    }
}
