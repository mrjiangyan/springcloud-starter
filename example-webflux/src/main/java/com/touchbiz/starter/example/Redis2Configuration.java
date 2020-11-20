package com.touchbiz.starter.example;

import com.touchbiz.cache.starter.RedisConfiguration;
import com.touchbiz.cache.starter.RedisLettuceTemplate;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

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
     * 配置redis连接工厂
     *
     * @return
     */
    public LettuceConnectionFactory cacheRedisConnectionFactory(Redis2Config config, @Qualifier("redis2Pool") GenericObjectPoolConfig redis2Pool) {
        //redis配置
        RedisStandaloneConfiguration redisConfiguration = new
                RedisStandaloneConfiguration(config.getHost(),config.getPort());
        redisConfiguration.setDatabase(config.getDatabase());
        redisConfiguration.setPassword(config.getPassword());

        //redis客户端配置
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder
                builder =  LettucePoolingClientConfiguration.builder().
                commandTimeout(Duration.ofMillis(timeOut));

        builder.shutdownTimeout(Duration.ofMillis(shutdownTimeOut));
        builder.poolConfig(redis2Pool);
        LettuceClientConfiguration lettuceClientConfiguration = builder.build();

        //根据配置和客户端配置创建连接
        LettuceConnectionFactory lettuceConnectionFactory = new
                LettuceConnectionFactory(redisConfiguration,lettuceClientConfiguration);
        lettuceConnectionFactory .afterPropertiesSet();

        return lettuceConnectionFactory;
    }

    /**
     * 配置redisTemplate 注入方式使用@Resource(name="") 方式注入
     *
     * @return
     */
    @Bean(name = "redisTemplate2")
    public RedisTemplate cacheRedisTemplate(Redis2Config config, GenericObjectPoolConfig redis2Pool) {
        return RedisConfiguration.template(cacheRedisConnectionFactory(config,redis2Pool.clone()));
    }
    @Bean(name = "redisLettuceTemplate2")
    public RedisLettuceTemplate template(RedisTemplate redisTemplate2){
        return new RedisLettuceTemplate(redisTemplate2);
    }

}
