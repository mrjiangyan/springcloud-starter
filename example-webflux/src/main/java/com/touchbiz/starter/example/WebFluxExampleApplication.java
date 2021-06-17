package com.touchbiz.starter.example;


import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.touchbiz.webflux.starter.configuration.feign.FeignConfiguration;
import com.touchbiz.webflux.starter.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
@EnableFeignClients(basePackages = {},defaultConfiguration = FeignConfiguration.class)
public class WebFluxExampleApplication extends BaseController {


    public static void main(String[] args) {
        SpringApplication.run(WebFluxExampleApplication.class, args);
    }

//    public static final String META_INF_RESOURCES = "classpath:/META-INF/resources/";
//    ImmutableMap<String,
//                String> webResourceMap = new ImmutableMap.Builder<String, String>().put("doc.html", META_INF_RESOURCES)
//            .put("/webjars/**", "classpath:/META-INF/resources/webjars/").put("/service-worker.js", META_INF_RESOURCES)
//            .put("/precache-manifest.*.js", META_INF_RESOURCES).build();
//
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        ImmutableSet<Map.Entry<String, String>> webResourceSet = webResourceMap.entrySet();
//        webResourceSet.forEach(x -> registry.addResourceHandler(x.getKey()).addResourceLocations(x.getValue()));
//    }



}
