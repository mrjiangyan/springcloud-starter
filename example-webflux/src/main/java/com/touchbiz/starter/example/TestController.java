package com.touchbiz.starter.example;

import com.touchbiz.cache.starter.annotation.MonoCacheable;
import com.touchbiz.cache.starter.annotation.NonReactorCacheable;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@Api("test")
public class TestController {

    @GetMapping
    public Mono<Object> get(final ServerHttpRequest request) {
        return Mono.just(request.getHeaders());
    }

    @NonReactorCacheable(keyPrefix = "AAA",redisKey = "#id")
    @GetMapping("/long")
    public Mono<Long> testLong(final ServerHttpRequest request) {
        return Mono.just(1089713253179727872L);
    }

    @ApiOperation("dddd")
    @PostMapping
    public Mono<ApiResult> post(@RequestBody AAA aaa) {
        log.info("aaa:{}", aaa);

        return Mono.justOrEmpty(ApiResult.getSuccessResponse());
    }

    @GetMapping("/test")
    public ApiResult test() throws Exception {
        throw new Exception("444");
    }

    @PostMapping("/test")
    public Mono<ApiResult> test(@RequestBody @Valid AAA aaa) {
        log.info("aaa:{}", aaa);
        return Mono.just(ApiResult.getSuccessResponse(aaa));
    }

    @SneakyThrows
    @GetMapping("/test/time/{id}")
    @NonReactorCacheable(keyPrefix = "AAA",redisKey = "#id")
    public ApiResult<AAA> testLocalDateTime(@PathVariable("id") @Validated @Max(10) Integer id) {
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        if(id>100){
            throw new BizException(LocalDateTime.now().toString());
        }
        return ApiResult.getSuccessResponse(a);
    }

    @GetMapping("/test/list")
    @MonoCacheable(keyPrefix = "REDIS", redisKey = "#id")
    public Mono<ApiResult<List<AAA>>> testList(@RequestParam Integer id) {
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        AAA b = new AAA();
        b.setTime(LocalDateTime.now().plusDays(1));
        return Mono.just(ApiResult.getSuccessResponse(Arrays.asList(a,b)));
    }

    @GetMapping("/test/list1")
    @NonReactorCacheable(keyPrefix = "AAA",redisKey = "#id")
    public ApiResult<List<AAA>> testList1(@Valid @NotNull(message = "sdjksjf")Integer id) {
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        AAA b = new AAA();
        b.setTime(LocalDateTime.now().plusDays(1));
        return ApiResult.getSuccessResponse(Arrays.asList(a,b));
    }
}
