package com.touchbiz.webflux.starter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.server.i18n.LocaleContextResolver;


@Configuration
public class LocaleSupportConfig extends DelegatingWebFluxConfiguration {

    @Autowired
    private LocaleResolver localeResolver;

    @Override
    protected LocaleContextResolver createLocaleContextResolver() {
        return localeResolver;
    }

}