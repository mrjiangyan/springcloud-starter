package com.touchbiz.cache.starter.reactor;

import reactor.core.publisher.Flux;

/**
 * Flux cache service
 *
 * @param <T> The Flux type of return value
 * @author Minkiu Kim
 */
public interface FluxCache<T> extends ReactorCache<Flux<T>, Flux<T>> {

    /**
     * Find Flux cache entity for the given key.
     *
     * @param retriever The Flux type retriever
     * @param config
     * @return The Flux type cache entity
     */
    @Override
    Flux<T> find(Flux<T> retriever, InternalCacheConfig config);

}