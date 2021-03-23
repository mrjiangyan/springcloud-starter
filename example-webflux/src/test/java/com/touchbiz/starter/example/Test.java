package com.touchbiz.starter.example;

import io.netty.handler.codec.http.HttpScheme;

public class Test {

    @org.junit.Test
    public void contextLoads() {
        assert HttpScheme.HTTP.name().toString().equals("http");
    }
}
