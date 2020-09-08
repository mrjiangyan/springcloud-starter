package com.touchbiz.mq.starter.configuration;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.touchbiz.mq.starter.RocketProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author steven
 */
@Slf4j
@Configuration
@RefreshScope
public class MqConfiguration {

    @Value("${spring.application.name}")
    private String groupId;

    /**
     * 对配置进行加解密处理
     */
    @Value("${cryptoConfig:true}")
    private Boolean cryptoConfig;

    @Bean(initMethod = "init", destroyMethod = "close")
    public RocketProducer producerFactory(RocketMqConfig config) {
        return new RocketProducer(config);
    }

    @Bean
    @ConfigurationProperties(prefix = "mq")
    public RocketMqConfig rocketMqConfig(NacosConfigProperties nacosConfigProperties) throws NacosException {
        RocketMqConfig config = new RocketMqConfig();
        String configInfo = nacosConfigProperties.configServiceInstance().getConfig("MQ_CONFIG", groupId, 3000);
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(configInfo));
            config.setAccessKey(properties.getProperty("accessKey"));
            config.setSecretKey(properties.getProperty("secretKey"));
            config.setGroupId(properties.getProperty("groupId"));
            config.setNamedAddress(properties.getProperty("namedAddress"));
        } catch (IOException e) {
            log.error("rocketMqConfig->error->", e);
        }
        return config;
    }

}
