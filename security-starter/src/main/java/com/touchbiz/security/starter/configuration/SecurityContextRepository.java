package com.touchbiz.security.starter.configuration;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SecurityContextRepository implements WebFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //查询用户的权限;
        var permission = new String[]{"/test/time/*"};
        Boolean exist = false;
        for (String s : permission) {
            PathPattern pattern = new PathPatternParser().parse(s);
            if (pattern.matches(request.getPath().pathWithinApplication()) ) {
                //权限校验通过
                log.warn("权限校验通过:{}", request.getPath().pathWithinApplication());
                exist = true;
                break;
            }
        }
        if(!exist){
            log.warn("权限校验没有通过:{}", request.getPath().pathWithinApplication());
        }
        return chain.filter(exchange);
    }
}
