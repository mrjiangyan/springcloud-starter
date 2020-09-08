package com.touchbiz.web.starter.controller;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @Author: Steven Jiang(mrjiangyan@hotmail.com)
 * @Date: 2018/9/28 12：00
 */
@Slf4j
@Data
public abstract class BaseController {

    @Autowired
    protected HttpRequest request;

    @Autowired
    protected HttpResponse response;

}
