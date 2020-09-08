package com.touchbiz.web.starter.servlet;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.stereotype.Component;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpResources;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.time.Duration;

@Slf4j
@Component
public class GracefulShutdown {

    @Autowired
    ReactorResourceFactory reactorResourceFactory;

    LoopResources loopResources;

    // SpringBoot 2.1.5 reactor.netty.resources.LoopResources#dispose 只 subscribe 没有 block 造成没有等待关闭，这边手工调用，后面如果修复了直接删除就好
    @PostConstruct
    void init() throws Exception {
        Field field = TcpResources.class.getDeclaredField("defaultLoops");
        field.setAccessible(true);
        loopResources = (LoopResources) field.get(reactorResourceFactory.getLoopResources());
        field.setAccessible(false);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Graceful: block long to 20s before real shutdown!");
            loopResources.disposeLater().block(Duration.ofSeconds(20));
        }));
    }
}
