package com.touchbiz.cache.starter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.var;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.lettuce.time-between-eviction-runs:1000}")
    private int timeBetweenEvictionRuns;

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig redisPool(RedisConfig config) {
        var poolConfig =  new GenericObjectPoolConfig();
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns);
        return poolConfig;
    }

    @Bean
    public RedisStandaloneConfiguration redisConf(RedisConfig config) {
        RedisStandaloneConfiguration redisConfiguration = new
                RedisStandaloneConfiguration(config.getHost(), config.getPort());
        redisConfiguration.setDatabase(config.getDatabase());
        redisConfiguration.setPassword(config.getPassword());
        return redisConfiguration;
    }

    @Bean
    @Primary
    public LettuceConnectionFactory factory(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConf) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(config).commandTimeout(Duration.ofMillis(timeout)).build();
        return new LettuceConnectionFactory(redisConf, clientConfiguration);
    }

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