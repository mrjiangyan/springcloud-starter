//package com.touchbiz.security.starter.configuration;
//
//import com.touchbiz.common.entity.result.ApiResult;
//import com.touchbiz.common.entity.result.IResultMsg;
//import com.touchbiz.common.utils.tools.JsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.netty.ByteBufFlux;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//@Slf4j
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
//public class SecurityConfiguration {
//
//    @Autowired
//    private ReactiveAuthenticationManager authenticationManager;
//
//    @Autowired
//    private ServerSecurityContextRepository securityContextRepository;
//
//    //security的鉴权排除列表
//    private static final String[] excludedAuthPages = {
//            "/auth/login",
//            "/auth/logout",
//            "/test/time/**"
//    };
//
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    @Bean
//    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {
//
//        return http
//                .exceptionHandling()
//                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
//                    swe.getResponse().setStatusCode(HttpStatus.OK);
//                    swe.getResponse().getHeaders().put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
//                    swe.getResponse().getHeaders().put(HttpHeaders.VARY, Arrays.asList("Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
//                    swe.getResponse().writeAndFlushWith(Flux.just(ByteBufFlux.just(swe.getResponse().bufferFactory()
//                            .wrap(JsonUtils.toJson(ApiResult.getCustomResponse(IResultMsg.APIEnum.NOT_LOGIN_ERROR)).getBytes()))));
//                })).accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> {
//                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//                    log.info("accessDeniedHandler");
//                })).and()
//                .csrf().disable()
//                .formLogin().disable()
//                .httpBasic().disable()
//                .authenticationManager(authenticationManager)
//                .securityContextRepository(securityContextRepository)
//                .authorizeExchange()
//                .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                .pathMatchers(excludedAuthPages).permitAll()
//                .anyExchange().authenticated()
//                .and().build();
//    }
//
//}