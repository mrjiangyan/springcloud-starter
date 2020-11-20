package com.touchbiz.cache.starter.reactor;

import com.touchbiz.cache.starter.IRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Mono cache implementation for spring cache
 *
 * @param <T> The Mono type of return value
 * @author Minkiu Kim
 */
@Slf4j
public class SpringMonoCache<T> extends AbstractSpringRedisCache<T> implements MonoCache<T> {

    /**
     * Constructor
     *
     * @param redisTemplate The spring redisTemplate
     * @param type  The Class of region cache type
     */
    public SpringMonoCache(com.touchbiz.cache.starter.IRedisTemplate redisTemplate, Class<T> type) {
        super(redisTemplate, type);
    }

    /**
     * Find Mono cache entity for the given key.
     *
     * @param retriever The Mono type retriever
     * @param config
     * @return The Mono type cache entity
     */
    @Override
    public Mono<T> find(Mono<T> retriever, InternalCacheConfig config) {
        return generactorCacheWriter(retriever,config);
    }





}