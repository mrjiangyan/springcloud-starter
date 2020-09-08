//package com.touchbiz.log.starter;
//
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConditionalOnWebApplication
//public class HttpTraceConfiguration {
//
//    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
//    static class ServletTraceFilterConfiguration {
//
//        @Bean
//        public HttpTraceLogFilter httpTraceLogFilter() {
//            return new HttpTraceLogFilter();
//        }
//
//    }
//
//}