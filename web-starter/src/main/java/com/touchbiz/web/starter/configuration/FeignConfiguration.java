//package com..web.starter.configuration;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//
////@Configuration
//public class FeignConfiguration implements RequestInterceptor {
//
//    @Override
//    public void apply(RequestTemplate template) {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
//                .getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        template.header(HttpHeaderConstants.HEADER_PROJECT, request.getHeader(HttpHeaderConstants.HEADER_PROJECT));
//    }
//}
