package com.touchbiz.webflux.starter.configuration;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RecorderServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    private DataBufferWrapper data = null;

    public RecorderServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @NotNull
    @Override
    public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
        return DataBufferUtilFix.join(Flux.from(body))
                .doOnNext(d -> this.data = d)
                .flatMap(d -> super.writeWith(copy()));
    }

    @NotNull
    @Override
    public Mono<Void> writeAndFlushWith(@NotNull Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body)
                .flatMapSequential(p -> p));
    }

    public Flux<DataBuffer> copy() {
        //如果data为null 就出错了 正好可以调试
        DataBuffer buffer = this.data.newDataBuffer();
        if (buffer == null) {
            return Flux.empty();
        }
        return Flux.just(buffer);
    }
}