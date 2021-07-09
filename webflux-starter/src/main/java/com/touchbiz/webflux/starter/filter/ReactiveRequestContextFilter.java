package com.touchbiz.webflux.starter.filter;

import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.Map;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRequestContextFilter implements WebFilter , Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ReactiveRequestContextHolder.put(request);
//        if(exchange.getAttributes() != null){
//            Map map = exchange.getAttributes();
//            if(map.containsKey(HttpHeaderConstants.HEADER_USER)){
//                ReactiveRequestContextHolder.putUser(map.get(HttpHeaderConstants.HEADER_USER));
//            }
//            if(map.containsKey(HttpHeaderConstants.HEADER_CHANNEL)){
//                ReactiveRequestContextHolder.putChannel(Integer.parseInt(map.get(HttpHeaderConstants.HEADER_CHANNEL).toString()));
//            }
//        }
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    if (SignalType.ON_COMPLETE == signalType) {
                        ReactiveRequestContextHolder.remove();
                    }
                });
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
