package com.touchbiz.db.starter.mybatis.interceptor;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

/**
 * 动态数据源切换拦截器
 *
 * 测试：拦截参数，自动切换数据源
 * 未来规划：后面通过此机制，实现多租户切换数据源功能
 * @author zyf
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class DynamicDatasourceInterceptor implements WebFilter, Ordered {




    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestURI = exchange.getRequest().getURI().toString();
        log.info("经过多数据源Interceptor,当前路径是{}", requestURI);
        //获取动态数据源名称
        String dsName = exchange.getRequest().getQueryParams().getFirst("dsName");
        String dsKey = "master";
        if (StringUtils.isNotEmpty(dsName)) {
            dsKey = dsName;
        }
        DynamicDataSourceContextHolder.push(dsKey);
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    if (SignalType.ON_COMPLETE == signalType) {
                        DynamicDataSourceContextHolder.clear();
                    }
                });
    }
}