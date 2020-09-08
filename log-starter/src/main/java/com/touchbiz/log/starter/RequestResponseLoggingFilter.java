//package com.touchbiz.log.starter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.reactivestreams.Publisher;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.nio.channels.Channels;
//
//@Slf4j
//public class RequestResponseLoggingFilter implements WebFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        ServerHttpRequest httpRequest = exchange.getRequest();
//        final String httpUrl = httpRequest.getURI().toString();
//
//        ServerHttpRequestDecorator loggingServerHttpRequestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
//            String requestBody = "";
//
//            @Override
//            public Flux<DataBuffer> getBody() {
//                return super.getBody().doOnNext(dataBuffer -> {
//                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//                        Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
//                        requestBody = IOUtils.toString(byteArrayOutputStream.toByteArray(), "UTF-8");
//                        log.info("{}",requestBody);
//                    } catch (IOException e) {
//                        log.error("{},{}", requestBody, e);
//                    }
//                });
//            }
//        };
//
//        ServerHttpResponseDecorator loggingServerHttpResponseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
//            String responseBody = "";
//            @Override
//            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                Mono<DataBuffer> buffer = Mono.from(body);
//                return super.writeWith(buffer.doOnNext(dataBuffer -> {
//                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//                        Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
//                        responseBody = IOUtils.toString(byteArrayOutputStream.toByteArray(), "UTF-8");
//                        log.info("{}",responseBody);
//                    } catch (IOException e) {
//                        log.error("{},{}", responseBody, e);
//                    }
//                }));
//            }
//        };
//        return chain.filter(exchange.mutate().request(loggingServerHttpRequestDecorator).response(loggingServerHttpResponseDecorator).build());
//    }
//
//}