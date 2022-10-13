package com.touchbiz.starter.example;

import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.webflux.starter.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Random;


@Slf4j
@RestController
@Api
public class TestController extends BaseController {

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/test")
    public ApiResult<String> test() {
        log.info("OK");
        testMapper.selectList(null).forEach(System.out::println);
        Test test = testMapper.selectById(1L);
        test.setAge(new Random().nextInt(100));
        testMapper.updateById(test);
        return ApiResult.getSuccessResponse("OK");
    }

    @PostMapping("/test2")
    public Mono<ApiResult> post(@ApiIgnore ServerHttpRequest request, @RequestBody @Validated Test test) {
        log.info("test:{}", test);
        test = testMapper.selectById(test.getId());
        test.setAge(new Random().nextInt(100));
        testMapper.updateById(test);
        return Mono.justOrEmpty(ApiResult.getSuccessResponse());
    }


}
