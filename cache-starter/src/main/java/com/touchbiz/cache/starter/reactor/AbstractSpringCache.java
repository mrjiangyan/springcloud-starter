package com.touchbiz.cache.starter.reactor;

import org.springframework.util.Assert;

/**
 * Base Reactor cache implementation for spring cache
 *
 * @param <T> The type of return value
 * @author Minkiu Kim
 */
abstract class AbstractSpringCache<T> {

    /**
     * Class of region cache type
     */
    protected Class<T> type;

    /**
     * Constructor
     *
     * @param type The Class of region cache type
     */
    protected AbstractSpringCache(Class<T> type) {
        Assert.notNull(type, "Class of region cache type must not be null");
        this.type = type;
    }


}