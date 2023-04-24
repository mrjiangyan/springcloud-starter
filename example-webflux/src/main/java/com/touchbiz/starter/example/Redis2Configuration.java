package com.touchbiz.starter.example;

import com.touchbiz.cache.starter.RedisLettuceTemplate;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

//@Configuration
public class Redis2Configuration {

    @Value("${spring.redis2.timeout}")
    private Long timeOut;

    @Value("${spring.redis2.lettuce.shutdown-timeout}")
    private Long shutdownTimeOut;

    @Bean("redis2Pool")
    @ConfigurationProperties(prefix = "spring.redis2.lettuce.pool")
    public BaseObjectPoolConfig redisPool(){
        return new GenericObjectPoolConfig();
    }

    /**
     * 配置redisTemplate 注入方式使用@Resource(name="") 方式注入
     *
     * @return
     */
//    @Bean(name = "redisTemplate2")
//    public RedisTemplate cacheRedisTemplate(Redis2Config config,  GenericObjectPoolConfig redis2Pool) {
//        return RedisConfiguration.template(cacheRedisConnectionFactory(config,redis2Pool.clone()));
//    }

    @Bean(name = "redisLettuceTemplate2")
    public RedisLettuceTemplate template(RedisTemplate redisTemplate2){
        return new RedisLettuceTemplate(redisTemplate2);
    }

}
