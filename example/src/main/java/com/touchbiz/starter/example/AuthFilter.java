package com.touchbiz.starter.example;

import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


/**
 * 验证token
 */
@Component
@Slf4j
@AllArgsConstructor
public class AuthFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        AAA a = new AAA();
        a.setData("122");
        exchange.getAttributes().put(HttpHeaderConstants.HEADER_USER, a);
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
