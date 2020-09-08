//package com.touchbiz.web.starter.servlet;
//
////import com.touchbiz.web.starter.servlet.GracefulShutdownTomcat;
//
//import io.undertow.Undertow;
//import io.undertow.UndertowOptions;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ServletConfig {
//
////    @ConditionalOnClass(TomcatServletWebServerFactory.class)
////    @Bean
////    public ServletWebServerFactory servletContainer(GracefulShutdownTomcat shutdown) {
////        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
////        tomcat.addConnectorCustomizers(shutdown);
////        return tomcat;
////    }
//
//    @ConditionalOnClass(Undertow.class)
//    @Bean
//    public UndertowServletWebServerFactory servletWebServerFactory(GracefulShutdownUndertowWrapper wapper) {
//        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
//        factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo.addOuterHandlerChainWrapper(wapper));
//        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, true));
//        return factory;
//    }
//
//}
