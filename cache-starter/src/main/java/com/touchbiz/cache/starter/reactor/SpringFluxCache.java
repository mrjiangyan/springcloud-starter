package com.touchbiz.cache.starter.reactor;

import reactor.core.publisher.Flux;

/**
 * Flux cache implementation for spring cache
 *
 * @param <T> The Flux type of return value
 * @author Minkiu Kim
 */
public class SpringFluxCache<T> extends AbstractSpringCache<T> implements FluxCache<T> {

    /**
     * Constructor
     *
     * @param type The spring cache
     * @param type  The Class of region cache type
     */
    public SpringFluxCache(Class<T> type) {
        super(type);
        throw new NotSupportException("FluxCache is not support");
    }

    /**
     * Find Flux cache entity for the given key.
     *
     * @param retriever The Flux type retriever
     * @param config
     * @return The Flux type cache entity
     */
    @Override
    public Flux<T> find(Flux<T> retriever, InternalCacheConfig config) {
        throw new NotSupportException("FluxCache is not support");
    }
}