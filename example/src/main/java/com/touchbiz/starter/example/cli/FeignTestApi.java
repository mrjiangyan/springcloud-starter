package com.touchbiz.starter.example.cli;

import com.touchbiz.starter.example.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "com-example2")
public interface FeignTestApi {
    //test
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test();
}

