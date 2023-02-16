package com.touchbiz.starter.example;

import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.query.BaseQuery;
import com.touchbiz.common.entity.result.MonoResult;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.time.LocalDateTime;

@Slf4j
@RestController
@Api
public class TestController extends BaseController {

    @ApiOperation("dddd")
    @PostMapping
    public MonoResult post(@ApiIgnore ServerHttpRequest request, @RequestBody @Validated AAA aaa) {
        log.info("aaa:{}", aaa);
        return MonoResult.ok();
    }


    @PostMapping("/test")
    public MonoResult<AAA> test(@RequestBody @Valid BaseQuery query) {
        log.info("query:{}", query);
        AAA aaa = new AAA();
        aaa.setTime(LocalDateTime.now());
        log.info("response:{}", JsonUtils.toJson(aaa));
        return MonoResult.ok(aaa);
    }

    @PostMapping("/form")
    public String form(@RequestParam("name") String name,
                             @RequestParam("age") Integer age) {
      return "name：" + name + "\nage：" + age;
    }

    @SneakyThrows
    @GetMapping("/test/time/{id}")
//    @RedisCache(keyPrefix = "AAA",redisKey = "#id")
    public MonoResult<AAA> testLocalDateTime(@PathVariable("id") @Validated @Max(10) Integer id) {
        AAA a = new AAA();
        a.setTime(LocalDateTime.now());
        if(id>100){
            throw new BizException(LocalDateTime.now().toString());
        }
        return MonoResult.ok(a);
    }

    @SneakyThrows
    @GetMapping("/test/cacheTime/{id}")
//    @RedisCache(keyPrefix = "AAA",redisKey = "#id")
    public String testCacheLocalDateTime(@PathVariable("id") @Validated @Max(10) Integer id) {
        return LocalDateTime.now().toString();
    }

}
