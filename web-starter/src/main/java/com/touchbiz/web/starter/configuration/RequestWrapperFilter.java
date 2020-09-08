//package com.touchbiz.web.starter.configuration;
//
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @Author: Steven Jiang(mrjiangyan@hotmail.com)
// * @Date: 2018/9/28 12：00
// */
//public class RequestWrapperFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        RequestWrapper requestWrapper = new RequestWrapper(request);
//        filterChain.doFilter(requestWrapper, response);
//    }
//}