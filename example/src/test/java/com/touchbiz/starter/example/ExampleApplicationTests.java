package com.touchbiz.starter.example;

import io.netty.handler.codec.http.HttpScheme;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExampleApplicationTests {

    @Test
    public void contextLoads() {
        assert HttpScheme.HTTP.name().toString().equals("http");
    }

}
