package com.touchbiz.webflux.starter.exception;

import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.common.entity.result.IResultMsg;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @SneakyThrows
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Throwable error = getError(request);
        ApiResult result;
        if (error instanceof ConversionNotSupportedException || error instanceof HttpMessageNotWritableException) {
            log.error("error:", error);
            result = ApiResult.getCustomResponse(IResultMsg.APIEnum.SERVER_ERROR);
        }
        else if(error instanceof ResponseStatusException
                && ((ResponseStatusException)error).getStatus() == HttpStatus.NOT_FOUND){
            log.error("error:{}", error.getMessage());
            result = ApiResult.getCustomResponse(IResultMsg.APIEnum.SERVER_ERROR);
        }
        else {
            log.error("error:", error);
            result = ApiResult.getErrorResponse(error);
        }

        Map<String, Object> map = new HashMap();
        Field[] fields = result.getClass().getDeclaredFields();

        //进行对象转map操作
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(result));
        }
        return map;
    }
}