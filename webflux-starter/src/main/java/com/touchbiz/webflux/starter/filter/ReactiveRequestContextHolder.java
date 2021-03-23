package com.touchbiz.webflux.starter.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ReactiveRequestContextHolder {
    private static final ThreadLocal<ServerHttpRequest> REQUEST_THREAD_LOCAL = new InheritableThreadLocal<>();

    private static final ThreadLocal<Object> USER_THREAD_LOCAL = new InheritableThreadLocal<>();

    private static final ThreadLocal<Integer> CHANNEL_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    public static final Class<Map> MAP_KEY = Map.class;

    /**
     * Gets the {@code Mono<ServerHttpRequest>} from Reactor {@link Context}
     * @return the {@code Mono<ServerHttpRequest>}
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(CONTEXT_KEY));
    }

    public static Mono<Map<String,Object>> getAttributes() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(MAP_KEY));
    }

    public static ServerHttpRequest get(){
        return REQUEST_THREAD_LOCAL.get();
    }

    public static Object getUser(){
        return USER_THREAD_LOCAL.get();
    }

    public static Integer getChannelId(){
        return CHANNEL_THREAD_LOCAL.get();
    }

    public static void put(ServerHttpRequest request){
        REQUEST_THREAD_LOCAL.set(request);
    }

    public static void putUser(Object user){
        USER_THREAD_LOCAL.set(user);
    }

    public static void putChannel(Integer channelId){
        CHANNEL_THREAD_LOCAL.set(channelId);
    }

    public static void remove(){
        REQUEST_THREAD_LOCAL.remove();
        USER_THREAD_LOCAL.remove();
        CHANNEL_THREAD_LOCAL.remove();
    }
}

