package com.touchbiz.webflux.starter.exception;

import com.touchbiz.common.entity.result.MonoResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

/**
 * 自定义异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExeptionHandler {
//
//    /**
//     * 400错误
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler({HttpMessageNotReadableException.class})
//    public ApiResult requestNotReadable(HttpMessageNotReadableException ex) {
//        log.error("HttpMessageNotReadableException", ex);
//        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
//    }
//
//    /**
//     * 400错误
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler({TypeMismatchException.class})
//    public ApiResult requestTypeMismatch(TypeMismatchException ex) {
//        log.error("TypeMismatchException", ex);
//        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
//    }


    /**
     * 500错误
     * @param ex
     * @return
     */
//    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
//    public MonoResult server500(RuntimeException ex) {
//        log.error("500...", ex);
//        return MonoResult.getCustomResponse(IResultMsg.APIEnum.SERVER_ERROR);
//    }



    /**
     * 处理自定义异常
     */
//    @ExceptionHandler(JeecgBootException.class)
//    public MonoResult<?> handleJeecgBootException(JeecgBootException e){
//        log.error(e.getMessage(), e);
//        return MonoResult.error(e.getMessage());
//    }

    /**
     * 处理自定义微服务异常
     */
//	@ExceptionHandler(JeecgCloudException.class)
//	public Result<?> handleJeecgCloudException(JeecgCloudException e){
//		log.error(e.getMessage(), e);
//		return Result.error(e.getMessage());
//	}

    /**
     * 处理自定义异常
     */
//	@ExceptionHandler(JeecgBoot401Exception.class)
//	@ResponseStatus(HttpStatus.UNAUTHORIZED)
//	public Result<?> handleJeecgBoot401Exception(JeecgBoot401Exception e){
//		log.error(e.getMessage(), e);
//		return new Result(401,e.getMessage());
//	}



    @ExceptionHandler(DuplicateKeyException.class)
    public MonoResult<?> handleDuplicateKeyException(DuplicateKeyException e){
        log.error(e.getMessage(), e);
        return MonoResult.error("数据库中已存在该记录");
    }



    @ExceptionHandler(Exception.class)
    public MonoResult<?> handleException(Exception e){
        log.error(e.getMessage(), e);
        return MonoResult.error("操作失败，"+e.getMessage());
    }

    @ExceptionHandler({WebExchangeBindException.class})
    public MonoResult<?> checkRequest(WebExchangeBindException e) {
        log.error(e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        final StringBuilder sb = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage()).append(";");
        }

        log.warn("WebExchangeBindException", e);
        return MonoResult.error("参数错误:" +  sb.substring(0, sb.length() - 1));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public MonoResult<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.warn("Illegal argument exception", e);
        return MonoResult.error("非法参数错误:" +  e.getMessage());
    }
    /**
     * @Author 政辉
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public MonoResult<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        StringBuilder sb = new StringBuilder();
        sb.append("不支持");
        sb.append(e.getMethod());
        sb.append("请求方法，");
        sb.append("支持以下");
        String [] methods = e.getSupportedMethods();
        if(methods!=null){
            for(String str:methods){
                sb.append(str);
                sb.append("、");
            }
        }
        log.error(sb.toString(), e);
        //return Result.error("没有权限，请联系管理员授权");
        return MonoResult.error(405,sb.toString());
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public MonoResult<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return MonoResult.error("文件大小超出10MB限制, 请压缩或降低文件质量! ");
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public MonoResult<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
//        log.error(e.getMessage(), e);
//        //【issues/3624】数据库执行异常handleDataIntegrityViolationException提示有误 #3624
//        return MonoResult.error("执行数据库异常,违反了完整性例如：违反惟一约束、违反非空限制、字段内容超出长度等");
//    }
//
//    @ExceptionHandler(PoolException.class)
//    public MonoResult<?> handlePoolException(PoolException e) {
//        log.error(e.getMessage(), e);
//        return MonoResult.error("Redis 连接异常!");
//    }

}