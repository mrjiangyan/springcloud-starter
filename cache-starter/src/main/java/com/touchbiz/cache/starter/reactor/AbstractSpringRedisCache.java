package com.touchbiz.cache.starter.reactor;

import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.Getter;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.function.Function;

/**
 * Base Reactor cache implementation for spring cache
 *
 * @param <T> The type of return value
 * @author Minkiu Kim
 */

abstract class AbstractSpringRedisCache<T> extends AbstractSpringCache<T> {

    @Getter
    private com.touchbiz.cache.starter.IRedisTemplate redisTemplate;

    /**
     * Constructor
     *
     * @param redisTemplate The spring redisTemplate
     * @param type  The Class of region cache type
     */
    public AbstractSpringRedisCache(com.touchbiz.cache.starter.IRedisTemplate redisTemplate, Class<T> type) {
        super(type);
        this.redisTemplate = redisTemplate;
    }


    /**
     * Mono Cache reader function
     */
    protected final Function<String, Mono<Signal<? extends T>>> reader = k -> Mono
            .fromCallable(() -> {
                Object value = getRedisTemplate().get(k);
                if (value != null) {
                    return JsonUtils.toObject(String.valueOf(value), type);
                }
                return null;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(t -> Mono.justOrEmpty(Signal.next(t)));

    protected  Mono<T> generactorCacheWriter(Mono<T> retriever, InternalCacheConfig config){
        return CacheMono.lookup(reader, config.getCacheKey())
                .onCacheMissResume(retriever)
                .andWriteWith((k, signal) -> Mono
                        .fromRunnable(() -> Optional.ofNullable(signal.get())
                                .ifPresent(o ->{
                                    boolean cacheData = true;
                                    boolean isError =false;
                                    if(config.getIgnoreError() && o instanceof ApiResult && !((ApiResult)o).isSuccess()){
                                        cacheData =false;
                                        isError = true;
                                    }
                                    if(cacheData){
                                        getRedisTemplate().set(k, JsonUtils.toJson(o), isError ? config.getErrorExpire() : config.getTimeout());
                                    }
                                }))
                        .subscribeOn(Schedulers.boundedElastic())
                        .then());
    }

}