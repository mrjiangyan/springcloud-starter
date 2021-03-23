package com.touchbiz.log.starter.alarm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchbiz.config.starter.configuration.MiddleSourceConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@RefreshScope
@Slf4j
public abstract class BaseMiddleProxy {

    @Autowired
    private MiddleSourceConfiguration.MiddleSourceConfig middleSourceConfig;

    /**
     * 此接口只能用于查询类操作，不适合用于业务处理类操作
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T generatorFeignClient(Class<T> clazz) {
        return generatorFeignClient(clazz, middleSourceConfig.getMiddleKey(), middleSourceConfig.getMiddleSecret());
    }

    private <T> T generatorFeignClient(Class<T> clazz, String key, String secret) {
        org.springframework.http.converter.HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(new ObjectMapper());
        ObjectFactory<org.springframework.boot.autoconfigure.http.HttpMessageConverters> converter = () -> new org.springframework.boot.autoconfigure.http.HttpMessageConverters(jsonConverter);

        return feign.Feign.builder()
                .encoder(new SpringEncoder(converter))
                .decoder(new SpringDecoder(converter))
                .requestInterceptor(template -> template.header("Content-Type", "application/json"))
                .requestInterceptor(template -> template.header("Accept", "application/json"))
                 .target(clazz, middleSourceConfig.getMiddleHost());
    }

    @Data
    public static class ChannelConfig {

        private String projectId;

        private String projectCode;

        private Integer projectKey;
    }
}
