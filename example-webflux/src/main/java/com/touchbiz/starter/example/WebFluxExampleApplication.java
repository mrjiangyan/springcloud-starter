package com.touchbiz.starter.example;


import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.webflux.starter.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Slf4j
@EnableWebFlux
@EnableCaching
@EnableAspectJAutoProxy
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "com.touchbiz.starter.example",
        "com.touchbiz.webflux.starter",
        "com.touchbiz.cache.starter",
        "com.touchbiz.config.starter",
        "com.alibaba.cloud.nacos",
//        "com.touchbiz.db.starter",
})
@RestController
@Api("test")
public class WebFluxExampleApplication extends BaseController {

    @Qualifier("redisLettuceTemplate")
    @Autowired
    private IRedisTemplate redisTemplate;

    @Qualifier("redisLettuceTemplate2")
    @Autowired
    private IRedisTemplate redisTemplate2;

    public static void main(String[] args) {
//        LocaleContextHolder.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        SpringApplication.run(WebFluxExampleApplication.class, args);
    }

    @GetMapping
    public Mono<Object> get(final ServerHttpRequest request) {
        return Mono.just(request.getHeaders());
    }

    @GetMapping("/long")
    public Mono<Long> testLong(final ServerHttpRequest request) {
        return Mono.just(1089713253179727872L);
    }

    @ApiOperation("dddd")
    @PostMapping
    public Mono<ApiResult> post(@RequestBody AAA aaa) throws Exception {
        log.info("aaa:{}", aaa);

        return Mono.justOrEmpty(ApiResult.getSuccessResponse());
    }

    @GetMapping("/test")
    public ApiResult test() throws Exception {
        throw new Exception("444");
    }

    @PostMapping("/test")
    public Mono<ApiResult> test(@RequestBody @Valid AAA aaa) throws Exception {
        log.info("aaa:{}", aaa);
        return Mono.just(ApiResult.getSuccessResponse(aaa));
    }

    @GetMapping("/test/time")
    public ApiResult<AAA> testLocalDateTime(@Valid @NotNull(message = "sdjksjf")Integer aaa) {
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
