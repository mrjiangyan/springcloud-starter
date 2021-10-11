package com.touchbiz.cache.starter.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchbiz.cache.starter.RedisConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "enable", havingValue = "true")
@ConditionalOnExpression(value = "'${spring.cache.type}'== 'redis'|| '${spring.cache.type}'=='caffeine-redis'")
@EnableConfigurationProperties(RedisProperties.class)
public class CacheRedisConfig {
    static {
        System.out.println("RedisConfig被创建");
    }

    @Bean
    public CustomeRedisCacheWriter customeRedisCacheWriter(RedisConnectionFactory connectionFactory) {
        return new CustomeRedisCacheWriter(connectionFactory);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return setSerializer(redisCacheConfiguration);
    }

    @Bean
    public RedisCacheManager redisCacheManager(CustomeRedisCacheWriter customeRedisCacheWriter, RedisCacheConfiguration redisCacheConfiguration) {
        System.out.println("redisCacheManager被创建");
        redisCacheConfiguration = redisCacheConfiguration
                .entryTtl(Duration.ofSeconds(1000))//有效期
                .disableCachingNullValues();//不缓存空值
        return new RedisCacheManager(customeRedisCacheWriter, redisCacheConfiguration);
    }

    private RedisCacheConfiguration setSerializer(RedisCacheConfiguration redisCacheConfiguration) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        redisCacheConfiguration = redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer));
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisConfiguration.getRedisSerializer()));
        return redisCacheConfiguration;
    }

}
