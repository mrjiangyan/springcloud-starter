package com.touchbiz.webflux.starter.filter;

import com.touchbiz.webflux.starter.configuration.LogUtil;
import com.touchbiz.webflux.starter.configuration.RecorderServerHttpRequestDecorator;
import com.touchbiz.webflux.starter.configuration.RecorderServerHttpResponseDecorator;
import io.netty.handler.codec.http.HttpScheme;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
//@Component
public class LowerRequestRecorderGlobalFilter implements WebFilter, Ordered {

    private final String[] pathIgnored = new String[]{"actuator","swagger","api-docs","doc.html","webjars"};

    private Mono<Void> finishLog(ServerWebExchange ex) {
        return LogUtil.recorderResponse(ex)
                .doOnSuccess(x -> log.info(LogUtil.getLogData(ex)));
    }

    @Override
    public int getOrder() {
        //在GatewayFilter之前执行
        return -1;
    }

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        if (originalRequest.getURI().getRawPath().contains("favicon.ico")) {
            return exchange.getResponse().setComplete();
        }
        URI originalRequestUrl = originalRequest.getURI();
        MediaType mediaType = originalRequest.getHeaders().getContentType();
        if(mediaType != null &&  mediaType.equals(MediaType.MULTIPART_FORM_DATA)){
            return chain.filter(exchange);
        }

        //只记录http的请求
        String scheme = originalRequestUrl.getScheme();
        if ((!HttpScheme.HTTP.name().toString().equals(scheme) && !HttpScheme.HTTPS.name().toString().equals(scheme))) {
            return chain.filter(exchange);
        }

        for(String path : pathIgnored){
            if (originalRequestUrl.getPath().contains(path)) {
                return chain.filter(exchange);
            }
        }

        String upgrade = originalRequest.getHeaders().getUpgrade();
        if ("websocket".equalsIgnoreCase(upgrade)) {
            return chain.filter(exchange);
        }


        // 在 GatewayFilter 之前执行， 此时的request时最初的request
        RecorderServerHttpRequestDecorator request = new RecorderServerHttpRequestDecorator(exchange.getRequest());

        // 此时的response时 发送回客户端的 response
        RecorderServerHttpResponseDecorator response = new RecorderServerHttpResponseDecorator(exchange.getResponse());

        ServerWebExchange ex = exchange.mutate()
                .request(request)
                .response(response)
                .build();

        return LogUtil.recorderOriginalRequest(ex)
                .then(Mono.defer(() -> chain.filter(ex)))
                .then(Mono.defer(() -> finishLog(ex)));
    }
}