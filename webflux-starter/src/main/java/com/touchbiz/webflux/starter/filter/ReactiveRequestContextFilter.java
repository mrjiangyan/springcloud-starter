package com.touchbiz.webflux.starter.filter;

import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ReactiveRequestContextHolder.put(request);
        if(exchange.getAttributes() != null){
            Map<String, Object> map = exchange.getAttributes();
            if(map.containsKey(HttpHeaderConstants.HEADER_USER)){
                ReactiveRequestContextHolder.putUser(map.get(HttpHeaderConstants.HEADER_USER));
            }
            if(map.containsKey(HttpHeaderConstants.HEADER_TENANT_ID)){
                ReactiveRequestContextHolder.putTenantId(String.valueOf(map.get(HttpHeaderConstants.HEADER_TENANT_ID)));
            }
        }
        var headers = exchange.getRequest().getHeaders();
        if(headers.containsKey(HttpHeaderConstants.HEADER_USER)){
            ReactiveRequestContextHolder.putUser(JsonUtils.toObject(headers.getFirst(HttpHeaderConstants.HEADER_USER),
                    SysUserCacheInfo.class));
        }
        if(headers.containsKey(HttpHeaderConstants.HEADER_TENANT_ID)){
            ReactiveRequestContextHolder.putTenantId(headers.getFirst(HttpHeaderConstants.HEADER_TENANT_ID));
        }
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
