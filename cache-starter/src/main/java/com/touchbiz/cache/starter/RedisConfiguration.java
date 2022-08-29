package com.touchbiz.cache.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean("redisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory, RedisSerializer redisSerializer) {
        return template(factory, redisSerializer);
    }

    @Bean("redisSerializer")
    public RedisSerializer getRedisSerializer(ObjectMapper objectMapper){
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    public RedisTemplate<String, Object> template(LettuceConnectionFactory factory, RedisSerializer redisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        var serializer = redisSerializer;
        // value序列化方式采用jackson
        template.setValueSerializer(serializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}