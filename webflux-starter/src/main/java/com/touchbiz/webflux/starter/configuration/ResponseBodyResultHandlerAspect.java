package com.touchbiz.webflux.starter.configuration;

// 切面bean,统一替换.

import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.common.entity.result.IResultMsg;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * webflux响应信息统一切换
 */
@Aspect
@Component
@ConditionalOnClass(name = {"org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler"})
public class ResponseBodyResultHandlerAspect {

    @Autowired(required = false)
    private LocaleMessage message;

    @SneakyThrows
    @Around(value = "execution(* org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler.handleResult(..)) && args(exchange, result)", argNames = "point,exchange,result")
    public Object handleResult(ProceedingJoinPoint point, ServerWebExchange exchange, HandlerResult result) {
        if(result.getReturnValue() instanceof Mono){
            final Mono<ApiResult> responseMono = ((Mono) result.getReturnValue()).map(responseValue -> responseValue instanceof ApiResult ? responseValue : ApiResult.getCustomResponse(IResultMsg.APIEnum.SERVER_ERROR,responseValue));
            //对该处做处理，启用多语言的相关控制
            if(message != null) {
                responseMono.subscribe(apiResult->{
                    apiResult.setMessage(message.getMessage("ERROR_"+ apiResult.getStatus(),apiResult.getMessage(),exchange));

                });
            }
            return point.proceed(Arrays.asList(
                    exchange,
                    new HandlerResult(result.getHandler(), responseMono, result.getReturnTypeSource())
            ).toArray());
        }
        else if(result.getReturnValue() instanceof ApiResult){
            ApiResult apiResult = (ApiResult) result.getReturnValue();
            apiResult.setMessage(message.getMessage("ERROR_" + apiResult.getStatus(),apiResult.getMessage(),exchange));
            return point.proceed(Arrays.asList(
                    exchange,
                    result
            ).toArray());
        }
        else{
            return point.proceed(Arrays.asList(
                    exchange,
                    result
            ).toArray());
        }

    }
}