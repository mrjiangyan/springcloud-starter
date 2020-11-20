package com.touchbiz.cache.starter.reactor;

/**
 * Not Support Exception
 *
 * @author Minkiu Kim
 */
public class NotSupportException extends RuntimeException {

    /**
     * Constructs a NotSupportException.
     *
     * @param message Message explaining the exception condition
     */
    public NotSupportException(String message) {
        super(message);
    }
}