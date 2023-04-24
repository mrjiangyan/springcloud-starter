package com.touchbiz.webflux.starter.configuration;


import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author steven
 */
public class LogUtil {

    private final static String REQUEST_RECORDER_LOG_BUFFER = "RequestRecorderGlobalFilter.request_recorder_log_buffer";

    private static boolean hasBody(HttpMethod method) {
        //只记录这3种谓词的body
        return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH;
    }

    private static boolean shouldRecordBody(MediaType contentType) {
        String type = contentType.getType();
        String subType = contentType.getSubtype();

        if ("application".equals(type)) {
            return "json".equals(subType) || "x-www-form-urlencoded".equals(subType) || "xml".equals(subType) || "atom+xml".equals(subType) || "rss+xml".equals(subType);
        } else {
            return "text".equals(type);
        }

        //form没有记录
    }

    private static Mono<Void> doRecordBody(StringBuilder logBuffer, Flux<DataBuffer> body, Charset charset) {
        logBuffer.append(",responseData:");
        return DataBufferUtilFix.join(body)
                .doOnNext(wrapper -> {
                    logBuffer.append(new String(wrapper.getData(), charset));
                    wrapper.clear();
                }).then();
    }

    private static Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
        if (mediaType != null && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        } else {
            return StandardCharsets.UTF_8;
        }
    }

    public static Mono<Void> recorderOriginalRequest(ServerWebExchange exchange) {
        StringBuilder logBuffer = new StringBuilder();
        exchange.getAttributes().put(REQUEST_RECORDER_LOG_BUFFER, logBuffer);

        ServerHttpRequest request = exchange.getRequest();
        return recorderRequest(request, request.getURI(), logBuffer.append("requestUrl:"));
    }

    private static Mono<Void> recorderRequest(ServerHttpRequest request, URI uri, StringBuilder logBuffer) {
        if (uri == null) {
            uri = request.getURI();
        }

        HttpMethod method = request.getMethod();
        HttpHeaders headers = request.getHeaders();

        logBuffer
                .append(method.toString()).append(' ')
                .append(uri);

        logBuffer.append(",requestHeader:");
        headers.forEach((name, values) -> values.forEach(value -> logBuffer.append(name).append(":").append(value).append(';')));

        Charset bodyCharset = null;
        if (hasBody(method)) {
            long length = headers.getContentLength();
            if (length >0) {
                MediaType contentType = headers.getContentType();
                if (contentType != null && shouldRecordBody(contentType)) {
                   bodyCharset = getMediaTypeCharset(contentType);
                }
            }
        }


        if (bodyCharset != null) {
            return doRecordBody(logBuffer, request.getBody(), bodyCharset);
        } else {
            return Mono.empty();
        }
    }

    public static Mono<Void> recorderResponse(ServerWebExchange exchange) {
        RecorderServerHttpResponseDecorator response = (RecorderServerHttpResponseDecorator) exchange.getResponse();
        StringBuilder logBuffer = exchange.getAttribute(REQUEST_RECORDER_LOG_BUFFER);

        HttpStatus code = response.getStatusCode();
        if (code == null) {
            return Mono.empty();
        }

        logBuffer.append(",responseCode:").append(code.value());

        HttpHeaders headers = response.getHeaders();
        logBuffer.append(",responseHeader:");
        headers.forEach((name, values) -> values.forEach(value -> logBuffer.append(name).append(":").append(value).append(';')));

        Charset bodyCharset = null;
        MediaType contentType = headers.getContentType();
        if (contentType == null) {
        } else if (!shouldRecordBody(contentType)) {

        } else {
            bodyCharset = getMediaTypeCharset(contentType);
        }

        if (bodyCharset != null) {
            return doRecordBody(logBuffer, response.copy(), bodyCharset);
        } else {
            return Mono.empty();
        }
    }

    public static String getLogData(ServerWebExchange exchange) {
        return exchange.getAttribute(REQUEST_RECORDER_LOG_BUFFER).toString();
    }
}