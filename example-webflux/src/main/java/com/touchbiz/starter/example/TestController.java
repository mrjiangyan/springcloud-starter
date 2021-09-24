package com.touchbiz.starter.example;

import com.touchbiz.cache.starter.annotation.MonoCacheable;
import com.touchbiz.cache.starter.annotation.RedisCache;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.query.BaseQuery;
import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.webflux.starter.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@Api
public class TestController extends BaseController {

    @ApiOperation("dddd")
    @PostMapping
    public Mono<ApiResult> post(@ApiIgnore ServerHttpRequest request, @RequestBody @Validated AAA aaa) {
        log.info("aaa:{}", aaa);
        return Mono.justOrEmpty(ApiResult.getSuccessResponse());
    }


    @PostMapping("/test")
    public Mono<ApiResult<BaseQuery>> test(@RequestBody @Valid BaseQuery query) {
        log.info("aaa:{}", query);
        query.setEndDate(LocalDateTime.now());
        return Mono.just(ApiResult.getSuccessResponse(query));
    }

    @SneakyThrows
    @GetMapping("/test/time/{id}")
    @RedisCache(keyPrefix = "AAA",redisKey = "#id")
    public ApiResult<AAA> testLocalDateTime(@PathVariable("id") @Validated @Max(10) Integer id) {
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        if(id>100){
            throw new BizException(LocalDateTime.now().toString());
        }
        return ApiResult.getSuccessResponse(a);
    }

    @SneakyThrows
    @GetMapping("/test/cacheTime/{id}")
    @RedisCache(keyPrefix = "AAA",redisKey = "#id")
    public String testCacheLocalDateTime(@PathVariable("id") @Validated @Max(10) Integer id) {
        return LocalDateTime.now().toString();
    }

    @SneakyThrows
    @GetMapping("/query")
    @RedisCache(keyPrefix = "AAA",redisKey = "#query")
    public ApiResult<AAA> query(AAA query) {
        return ApiResult.getSuccessResponse(query);
    }

    @GetMapping("/test/list")
    @MonoCacheable(keyPrefix = "REDIS", redisKey = "#id")
    Mono<ApiResult<List<AAA>>> testList(@RequestParam Integer id) {
        log.info("---------testList----------");
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        AAA b = new AAA();
        b.setTime(LocalDateTime.now().plusDays(1));
        ApiResult result =  ApiResult.getSuccessResponse(Arrays.asList(a,b));
        log.info("-----{}-----", result);
        return Mono.just(result);
    }

    @GetMapping("/test/list2")
    @MonoCacheable(keyPrefix = "REDIS", redisKey = "#id")
    private Mono<ApiResult<List<AAA>>> testList2(@RequestParam Integer id) {
        log.info("---------testList----------");
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        AAA b = new AAA();
        b.setTime(LocalDateTime.now().plusDays(1));
        ApiResult result =  ApiResult.getSuccessResponse(Arrays.asList(a,b));
        log.info("-----{}-----", result);
        return Mono.just(result);
    }

    @GetMapping("/test/list1")
    @RedisCache(keyPrefix = "AAA",redisKey = "#id")
    public ApiResult<List<AAA>> testList1(@Valid @NotNull(message = "sdjksjf")Integer id) {
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        AAA b = new AAA();
        b.setTime(LocalDateTime.now().plusDays(1));
        return ApiResult.getSuccessResponse(Arrays.asList(a,b));
    }
}
