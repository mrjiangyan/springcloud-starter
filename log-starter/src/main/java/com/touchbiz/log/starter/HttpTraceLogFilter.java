package com.touchbiz.log.starter;


import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;

/**
 * 记录http请求日志
 */
@Slf4j
@RefreshScope
public class HttpTraceLogFilter extends OncePerRequestFilter implements Ordered {

    private static final int ORDER = Ordered.LOWEST_PRECEDENCE - 10;

    private static final String IGNORE_CONTENT_TYPE = "multipart/form-data";

    @Value("${traceHttp:true}")
    private boolean traceHttp;

    @Value("${traceFilter:actuator,swagger}")
    private String[] filterList;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!isRequestValid(request) || HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestPath = request.getServletPath().split("\\?")[0];
        //过滤掉不打印的路径
        for (String noFilter : filterList) {
            if (requestPath.toLowerCase().contains(noFilter)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        String content = null;
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (traceHttp && !Objects.equals(IGNORE_CONTENT_TYPE, request.getContentType())) {
                HashMap<String, String> headerMap = new HashMap<>();
                Enumeration<String> headNames = request.getHeaderNames();
                while (headNames.hasMoreElements()) {
                    String header = headNames.nextElement();
                    headerMap.put(header, request.getHeader(header));
                }
                if (traceHttp && !Objects.equals(IGNORE_CONTENT_TYPE, request.getContentType())) {
                    if (!request.getMethod().equals(HttpMethod.GET.name())) {
                        content = new String(((ContentCachingRequestWrapper) request).getContentAsByteArray(), StandardCharsets.UTF_8);
                    }
                }
                log.trace(
                        "request:url={},\nmethod={},\nheader:{},\nparams={},\nresponse:{}",
                        requestPath,
                        request.getMethod(),
                        JsonUtils.toJson(headerMap), content, getResponseBody((ContentCachingResponseWrapper) response)
                );

            }
            updateResponse((ContentCachingResponseWrapper) response);
        }

    }

    private boolean isRequestValid(HttpServletRequest request) {
        try {
            new URI(request.getRequestURL().toString());
            return true;
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    /**
     * 打印请求参数
     */
    private String getResponseBody(ContentCachingResponseWrapper wrapper) {
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    payload = "[Unknow]";
                }
                return payload;
            }
        }
        return "";
    }

    private void updateResponse(ContentCachingResponseWrapper response) throws IOException {
        response.copyBodyToResponse();
    }

}