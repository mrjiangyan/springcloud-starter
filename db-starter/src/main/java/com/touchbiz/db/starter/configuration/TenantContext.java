package com.touchbiz.db.starter.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantContext {
    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);
    private static ThreadLocal<String> currentTenant = new ThreadLocal();

    public TenantContext() {
    }

    public static void setTenant(String tenant) {
        log.debug(" setting tenant to " + tenant);
        currentTenant.set(tenant);
    }

    public static String getTenant() {
        return (String)currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}

