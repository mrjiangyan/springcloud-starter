package com.touchbiz.cache.starter.reactor;

/**
 * Reactor cache service
 *
 * @param <R> Flux or Mono
 * @param <T> The Flux or Mono type of return value
 * @author Minkiu Kim
 */
public interface ReactorCache<R, T> {

    /**
     * Find reactor cache entity for the given key.
     *
     * @param retriever The retriever
     * @param config
     * @return Cache entity
     */
    T find(R retriever, InternalCacheConfig config);

}