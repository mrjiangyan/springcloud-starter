package com.touchbiz.webflux.starter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanConfiguration {

    @Bean
    public LocaleResolver localeResolver() {
        return new LocaleResolver();
    }

}
