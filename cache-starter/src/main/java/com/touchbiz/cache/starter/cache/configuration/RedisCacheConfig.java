package com.touchbiz.cache.starter.cache.configuration;

import com.touchbiz.cache.starter.RedisConfiguration;
import com.touchbiz.cache.starter.cache.CustomeRedisCacheWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "enable", havingValue = "true")
@ConditionalOnExpression(value = "'${spring.cache.type}'== 'redis'|| '${spring.cache.type}'=='caffeine-redis'")
@EnableConfigurationProperties(RedisProperties.class)
public class RedisCacheConfig {

    @Bean
    public CustomeRedisCacheWriter customeRedisCacheWriter(RedisConnectionFactory connectionFactory) {
        return new CustomeRedisCacheWriter(connectionFactory);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(RedisSerializer redisSerializer) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();

        redisCacheConfiguration = redisCacheConfiguration
                .serializeValuesWith(RedisSerializationContext
                .SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(Duration.ofSeconds(1000))//有效期
                .disableCachingNullValues();//不缓存空值;
        return redisCacheConfiguration;
    }

    @Bean
    public RedisCacheManager redisCacheManager(CustomeRedisCacheWriter customeRedisCacheWriter, RedisCacheConfiguration redisCacheConfiguration) {
        System.out.println("redisCacheManager被创建");
        return new RedisCacheManager(customeRedisCacheWriter, redisCacheConfiguration);
    }

}
