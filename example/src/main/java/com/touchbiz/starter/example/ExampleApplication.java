package com.touchbiz.starter.example;


import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.touchbiz.webflux.starter.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.stream.Collectors;

@Slf4j
@EnableWebFlux
@EnableAsync
@SpringBootApplication(scanBasePackages = {
        "com.touchbiz.starter.example",
        "com.touchbiz.webflux.starter",
        "com.touchbiz.cache.starter",
        "com.touchbiz.log.starter",
        "com.touchbiz.config.starter",
})

@EnableKnife4j
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableFeignClients(basePackages = {})
public class ExampleApplication extends BaseController {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

//    @Configuration
//    public class HttpMsgConverConfig {
//
//        @Bean
//        @ConditionalOnMissingBean
//        public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
//            return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
//        }
//    }

}
