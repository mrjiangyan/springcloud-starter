package com.touchbiz.web.starter.exception;

import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.exception.ParamException;
import com.touchbiz.common.entity.exception.RpcException;
import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.common.entity.result.IResultMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 自定义异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExeptionHandler {

    //400错误
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ApiResult requestNotReadable(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
    }

    //400错误
    @ExceptionHandler({TypeMismatchException.class})
    public ApiResult requestTypeMismatch(TypeMismatchException ex) {
        log.error("TypeMismatchException", ex);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
    }


    //405错误
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ApiResult request405(HttpRequestMethodNotSupportedException ex) {
        log.error("405...", ex);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
    }

    //406错误
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public ApiResult request406(HttpMediaTypeNotAcceptableException ex) {
        log.error("406...", ex);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
    }

    //500错误
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public ApiResult server500(RuntimeException ex) {
        log.error("500...", ex);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.SERVER_ERROR);
    }

    @ExceptionHandler(BizException.class)
    public ApiResult<Void> bizExceptionHandle(BizException e) {
        log.error("BizException error", e);
        return ApiResult.getCustomResponse(e.getMsg());
    }

    @ExceptionHandler(ParamException.class)
    public ApiResult paramExceptionHandle(ParamException e) {
        log.error("ParamException error", e);
        return ApiResult.getCustomResponse(e.getMsg());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.warn("Illegal argument exception", e);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.PARAM_ERROR, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.warn("Required parameter missing", e);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.PARAM_ERROR, e.getMessage());
    }

//    @ExceptionHandler
//    public ApiResult handleResourceNotFoundException(NoHandlerFoundException nhre) {
//        log.error(nhre.getMessage(), nhre);
//        return ApiResult.getErrorResponse(nhre);
//    }

    @ExceptionHandler(RpcException.class)
    public ApiResult rpcExceptionHandler(RpcException e) {
        log.error("Rpc exception", e);
        return ApiResult.getCustomResponse(e.getMsg());
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult runtimeExceptionHandle(RuntimeException e) {
        log.error("RuntimeException caught", e);
        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult exceptionHandle(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;
            try {
                List<ObjectError> errors = validException.getBindingResult().getAllErrors();
                if (!CollectionUtils.isEmpty(errors) && errors.get(0) instanceof FieldError) {
                    FieldError fieldError = (FieldError) errors.get(0);
                    log.warn("Param caught", e);
                    return ApiResult.getCustomResponse(IResultMsg.APIEnum.PARAM_ERROR, fieldError.getDefaultMessage());
                }
            } catch (Exception ie) {
                log.warn("Handle the MethodArgumentNotValidException error!", ie);
            }
        }
        log.error("Exception caught", e);
        return ApiResult.getErrorResponse(e);
    }
}