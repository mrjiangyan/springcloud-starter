package com.touchbiz.cache.starter.reactor;

import reactor.core.publisher.Mono;

/**
 * Mono cache service
 *
 * @param <T> The Mono type of return value
 * @author Minkiu Kim
 */
public interface MonoCache<T> extends ReactorCache<Mono<T>, Mono<T>> {

    /**
     * Find Mono cache entity for the given key.
     *
     * @param retriever The Mono type retriever
     * @param config
     * @return The Mono type cache entity
     */
    @Override
    Mono<T> find(Mono<T> retriever, InternalCacheConfig config);

}