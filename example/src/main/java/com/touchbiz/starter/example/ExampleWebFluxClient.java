package com.touchbiz.starter.example;

import com.touchbiz.common.entity.query.BaseQuery;
import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.webflux.starter.configuration.feign.FeignConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@FeignClient(name = "com.example.webflux", configuration= FeignConfiguration.class)
public interface ExampleWebFluxClient {

    @PostMapping("/test")
    ApiResult<AAA> test(@RequestBody BaseQuery query);
}

