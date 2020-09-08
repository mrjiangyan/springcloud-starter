//package com.touchbiz.web.starter.servlet;
//
//
//import io.undertow.server.HandlerWrapper;
//import io.undertow.server.HttpHandler;
//import io.undertow.server.handlers.GracefulShutdownHandler;
//import org.springframework.stereotype.Component;
//
///**
// *  * @Author: heks
// *  * @DATE: 2019/3/15 12:59 PM
// *  
// */
//@Component
//public class GracefulShutdownUndertowWrapper implements HandlerWrapper {
//
//    private GracefulShutdownHandler gracefulShutdownHandler;
//
//    @Override
//    public HttpHandler wrap(HttpHandler handler) {
//        if (gracefulShutdownHandler == null) {
//            this.gracefulShutdownHandler = new GracefulShutdownHandler(handler);
//        }
//        return gracefulShutdownHandler;
//    }
//
//    public GracefulShutdownHandler getGracefulShutdownHandler() {
//        return gracefulShutdownHandler;
//    }
//}
