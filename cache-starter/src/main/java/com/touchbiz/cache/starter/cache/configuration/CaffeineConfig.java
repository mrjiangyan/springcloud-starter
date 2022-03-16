package com.touchbiz.cache.starter.cache.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "enable", havingValue = "true")
@ConditionalOnExpression(value = "'${spring.cache.type}'== 'caffeine'|| '${spring.cache.type}'=='caffeine-redis'")
public class CaffeineConfig {

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        System.out.println("caffeineCacheManager被创建");
        Caffeine caffeine = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                //最大weight值，当所有entry的weight和快达到这个限制的时候会发生缓存过期，剔除一些缓存
//                .maximumWeight(1000)
                //每个 Entry 的 weight 值
//                .weigher(new Weigher<String, List<Object>>() {
//                    @Override
//                    public int weigh(@NonNull String key, @NonNull List<Object> value) {
//                        log.info("key:{}", key);
//                        return value.size();
//                    }
//                })
//                .refreshAfterWrite(15,TimeUnit.SECONDS)
                .expireAfterWrite(30, TimeUnit.SECONDS);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(true);
        cacheManager.setCaffeine(caffeine);
//        cacheManager.setCacheLoader(cacheLoader);
//        cacheManager.setCacheNames(getNames());
        return cacheManager;
    }
}
