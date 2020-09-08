package com.touchbiz.webflux.starter.exception;

import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.common.entity.result.IResultMsg;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @SneakyThrows
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return assembleError(request);
    }

    private Map<String, Object> assembleError(ServerRequest request) throws Exception {
        Throwable error = getError(request);
        log.error("error:", error);
        ApiResult result;
        if (error instanceof ConversionNotSupportedException || error instanceof HttpMessageNotWritableException) {
            result = ApiResult.getCustomResponse(IResultMsg.APIEnum.SERVER_ERROR);
        } else {
            result = ApiResult.getErrorResponse(error);
        }
        return JsonUtils.json2map(JsonUtils.toJson(result));

    }

}