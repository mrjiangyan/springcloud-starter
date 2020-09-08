package com.touchbiz.starter.example;


import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.webflux.starter.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@EnableCaching
@EnableAspectJAutoProxy
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "com.touchbiz.webflux.starter",
        "com.touchbiz.cache.starter",
        "com.alibaba.cloud.nacos",
//        "com.touchbiz.db.starter",
        "com.touchbiz.log.starter"
})
@RestController
public class ExampleApplication extends BaseController {

    @Value("${test}")
    private String testValue;

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }


    @GetMapping("/test")
    public ApiResult test() throws Exception {
        throw new Exception("444");
    }

    @PostMapping("/test")
    public ApiResult test(@RequestBody AAA aaa) throws Exception {
        log.info("aaa:{}", aaa);
        throw new Exception("444");
    }

    @GetMapping("/test/time")
    public ApiResult<AAA> testLocalDateTime() {
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        return ApiResult.getSuccessResponse(a);
    }


//    @Bean
//    public ObjectMapper serializingObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        objectMapper.registerModule(new JavaTimeModule());
//        return objectMapper;
//    }

//    @Bean
//    public Formatter<LocalDateTime> localDateTimeFormatter() {
//        return new Formatter<LocalDateTime>() {
//            @Override
//            public String print(LocalDateTime object, Locale locale) {
//                return object.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            }
//
//            @Override
//            public LocalDateTime parse(String text, Locale locale) throws ParseException {
//                return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            }
//        };
//    }


}
