package com.touchbiz.log.starter.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BusinessLogAspect {

    /**
     * 线程池 异步记录日志
     */
    private static ExecutorService logExecutorService =  Executors.newFixedThreadPool(10);

    @Pointcut("@annotation(com.example.demo.annotation.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //获取request  RequestContextHolder 是ThreadLocal 对象异步不是一个线程
//        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //注释下面可以直接响应出去 改变结果
        //HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //httpServletResponse.getWriter().print("ok");
        //return null;
        //保存日志 注意如果方法执行错误这不会记录日志
//        logExecutorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                saveSysLog(point, time,httpServletRequest);
//            }
//        });
        return result;
    }
}
