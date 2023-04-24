package com.touchbiz.webflux.starter.configuration;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
public class RecorderServerHttpRequestDecorator extends ServerHttpRequestDecorator {

    private DataBufferWrapper data = null;

    public RecorderServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }

    @NotNull
    @Override
    public Flux<DataBuffer> getBody() {
        synchronized (this) {
            Mono<DataBuffer> mono;
            if (data == null) {
                mono = DataBufferUtilFix.join(super.getBody())
                        .doOnNext(d -> this.data = d)
                        .filter(d -> d.getFactory() != null)
                        .map(DataBufferWrapper::newDataBuffer);
            } else {
                mono = Mono.justOrEmpty(data.newDataBuffer());
            }

            return Flux.from(mono);
        }
    }
}