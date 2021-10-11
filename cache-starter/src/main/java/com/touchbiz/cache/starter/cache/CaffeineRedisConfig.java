package com.touchbiz.cache.starter.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "enable", havingValue = "true")
@EnableCaching
public class CaffeineRedisConfig {
//    @Bean
//    public CustomKeyGenerator customKeyGenerator() {
//        return new CustomKeyGenerator();
//    }

    //选择使用redisCacheManager还是CaffeineCacheManager
    @Bean
    public CaffeineRedisCacheResolver customCaffeineRedisCacheResolver(@Qualifier(value = "customCaffeineRedisManager") CaffeineRedisManager cacheManager) {
        System.out.println("打印manager");
        return new CaffeineRedisCacheResolver(cacheManager);
    }

//  @Bean
//  public CustomCaffeineRedisCacheResolver customCaffeineRedisCacheResolver(@Qualifier(value = "redisCacheManager") RedisCacheManager cacheManager){
//    System.out.println("打印manager");
//    return new CustomCaffeineRedisCacheResolver(cacheManager);
//  }

    @Bean("customCaffeineRedisManager")
    public CaffeineRedisManager customCaffeineRedisManager(@Autowired(
            required = false
    ) RedisCacheManager redisCacheManager, @Autowired(
            required = false
    ) CaffeineCacheManager caffeineCacheManager) {
        System.out.println("创建manager");
        return new CaffeineRedisManager(caffeineCacheManager, redisCacheManager);
    }

    @Bean
    public CustomCachingConfigurer customCachingConfigurer(CaffeineRedisCacheResolver customCaffeineRedisCacheResolver) {
        CustomCachingConfigurer customCachingConfigurer = new CustomCachingConfigurer();
        //设置默认的CacheManager
        customCachingConfigurer.setCacheManager(new CaffeineCacheManager());
        customCachingConfigurer.setCacheResolver(customCaffeineRedisCacheResolver);
        // customCachingConfigurer.setKeyGenerator(customKeyGenerator);
        return customCachingConfigurer;
    }


}
