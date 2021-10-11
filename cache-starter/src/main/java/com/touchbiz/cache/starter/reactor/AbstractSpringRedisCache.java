package com.touchbiz.cache.starter.reactor;

import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

/**
 * Base Reactor cache implementation for spring cache
 *
 * @param <T> The type of return value
 * @author Minkiu Kim
 */

@Slf4j
abstract class AbstractSpringRedisCache<T> extends AbstractSpringCache<T> {

    @Getter
    private IRedisTemplate redisTemplate;

    /**
     * Constructor
     *
     * @param redisTemplate The spring redisTemplate
     * @param type  The Class of region cache type
     */
    public AbstractSpringRedisCache(IRedisTemplate redisTemplate, Class<T> type) {
        super(type);
        this.redisTemplate = redisTemplate;
    }


    /**
     * Mono Cache reader function
     */
    protected final Function<String, Mono<Signal<? extends T>>> reader = k -> Mono
            .fromCallable(() -> {
                log.info("----{}-----",k);
                Object value = getRedisTemplate().get(k);
                if (value != null) {
                    return JsonUtils.toObject(String.valueOf(value), type);
                }
                return null;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(t -> Mono.justOrEmpty(Signal.next(t)));

    protected Mono<T> generactorCacheWriter(Object retriever, InternalCacheConfig config){
        log.info("----{}-----",config.getCacheKey());
        Object value = getRedisTemplate().get(config.getCacheKey());
        if (value != null) {
            return Mono.just(JsonUtils.toObject(String.valueOf(value), type));
        }
        Mono<T> mono;
        if(retriever instanceof Mono){
            mono = (Mono<T>) retriever;
        }
        else if(retriever instanceof CacheOperationInvoker){
            value =  ((CacheOperationInvoker)retriever).invoke();
            if(value instanceof  Mono){
                mono = (Mono<T>) value;
            }
            else{
                mono = (Mono<T>) Mono.just(value);
            }
        }
        else{
            mono =   (Mono<T>) Mono.just(retriever);
        }
        return mono.doOnNext(o->{
            boolean cacheData = true;
            boolean isError =false;
            if(config.getIgnoreError() && o instanceof ApiResult && !((ApiResult)o).isSuccess()){
                cacheData =false;
                isError = true;
            }
            if(cacheData){
                getRedisTemplate().set(config.getCacheKey(), JsonUtils.toJson(o), isError ? config.getErrorExpire() : config.getTimeout());
            }
        });
    }

}