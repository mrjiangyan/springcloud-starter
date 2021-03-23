package com.touchbiz.cache.starter.reactor;

import com.touchbiz.cache.starter.IRedisTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Mono cache implementation for spring cache
 *
 * @param <T> The Mono type of return value
 * @author Minkiu Kim
 */
@Slf4j
public class SpringNonReactorCache<T> extends AbstractSpringRedisCache<T> implements ReactorCache<Object, T> {

    public SpringNonReactorCache(IRedisTemplate redisTemplate, Class<T> type) {
        super(redisTemplate, type);
    }

    /**
     * Find Mono cache entity for the given key.
     *
     * @param retriever The Mono type retriever
     * @param config
     * @return The Mono type cache entity
     */
    @SneakyThrows
    @Override
    public T find(Object retriever, InternalCacheConfig config) {
        return generactorCacheWriter(retriever,config).toFuture().get();

    }


}