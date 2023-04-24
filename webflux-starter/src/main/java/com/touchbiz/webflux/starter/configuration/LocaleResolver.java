package com.touchbiz.webflux.starter.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.util.Locale;

public class LocaleResolver implements LocaleContextResolver {

    @NotNull
    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
       return resolveLocaleContext(exchange.getRequest());
    }

    private LocaleContext resolveLocaleContext(ServerHttpRequest request) {
        String language = request.getHeaders().getFirst(HttpHeaders.ACCEPT_LANGUAGE);
        Locale targetLocale = Locale.getDefault();
        if (language != null && !language.isEmpty()) {
            targetLocale = Locale.forLanguageTag(language);
        }
        return new SimpleLocaleContext(targetLocale);
    }

    @Override
    public void setLocaleContext(@NotNull ServerWebExchange exchange, LocaleContext localeContext) {
        throw new UnsupportedOperationException("Not Supported");
    }

}
