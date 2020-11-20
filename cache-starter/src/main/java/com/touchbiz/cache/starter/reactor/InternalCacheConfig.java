package com.touchbiz.cache.starter.reactor;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author steven
 */
@Data
@AllArgsConstructor
public class InternalCacheConfig {

    private String cacheKey;

    private Boolean printLog;

    private int timeout;

    private Boolean ignoreError;

    private int errorExpire;

}
