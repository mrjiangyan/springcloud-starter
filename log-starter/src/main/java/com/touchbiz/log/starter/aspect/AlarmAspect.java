//package com.touchbiz.log.starter.aspect;
//
//
//import com.touchbiz.common.utils.date.DateTimeFormat;
//import com.touchbiz.common.utils.date.LocalDateTimeUtils;
//import com.touchbiz.log.starter.alarm.AlarmMiddleProxy;
//import com.touchbiz.log.starter.alarm.AlarmWechatConvert;
//import com.touchbiz.log.starter.alarm.AlarmWechatProperties;
//import com.touchbiz.log.starter.alarm.AlarmWechatRequest;
//import com.touchbiz.log.starter.annotation.Alarm;
//import lombok.extern.slf4j.Slf4j;
//import lombok.var;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.lang.reflect.Method;
//import java.util.Arrays;
//
///**
// * @author zhangxuezhen
// * @description: 异常标签切面处理器
// * @date 2020/12/102:05 下午
// */
//@Aspect
//@Component
//@Slf4j
//public class AlarmAspect {
//    /**
//     * @EnableCaching 启动类加
//     */
//    @Autowired
//    private AlarmMiddleProxy alarmMiddleProxy;
//
//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    @Value("${spring.profiles.active:未知环境}")
//    private String profiles;
//
//    private static final Integer MAX_LENGTH = 700;
//
//    /**
//     * 定义切点
//     */
//    @Around("@annotation(alarm)")
//    public Object around(ProceedingJoinPoint point, Alarm alarm) throws Throwable {
//        try {
//            return point.proceed();
//        } catch (Exception e) {
//            if (alarm != null) {
//                var buffer = AlarmWechatConvert.transformOutBuffer(applicationName);
//                //描述头
//                AlarmWechatConvert.append(buffer, "环境", profiles);
//                //报错位置
//                String className = point.getTarget().getClass().getName();
//                Method method = ((MethodSignature) (point.getSignature())).getMethod();
//                String str = "代码位置: " + className + "." + method.getName() ;
//                AlarmWechatConvert.append(buffer, str, profiles);
//                if (!StringUtils.isEmpty(alarm.message())) {
//                    AlarmWechatConvert.append(buffer, "自定义描述", alarm.message());
//                }
//                AlarmWechatConvert.append(buffer, "异常时间", LocalDateTimeUtils.getCurrentTimeStr(DateTimeFormat.DATE_FORMAT_FULL));
//                AlarmWechatConvert.append(buffer, "异常信息", e.toString());
//                if (e.getStackTrace() != null && e.getStackTrace().length > 0) {
//                    Arrays.stream(e.getStackTrace()).forEach(x -> {
//                                buffer.append(x);
//                                buffer.append("/n");
//                            }
//                    );
//                }
//                //@预警人
//                AlarmWechatRequest request = new AlarmWechatRequest();
//                if (!StringUtils.isEmpty(alarm.mentionedMobile())) {
//                    String[] split = alarm.mentionedMobile().split(",");
//                    request.setMentionedMobileList(Arrays.asList(split));
//                }
//                request.setMsgtype("text");
//                // 默认预警群设置
//                if ("pat".equals(profiles)) {
//                    request.setWebhook(AlarmWechatProperties.patWechat);
//                } else {
//                    request.setWebhook(AlarmWechatProperties.uatWechat);
//                }
//                if (!StringUtils.isEmpty(alarm.webhook())) {
//                    request.setWebhook(alarm.webhook());
//                }
//                String message = buffer.toString();
//                if (message.length() > MAX_LENGTH) {
//                    request.setMessage(message.substring(0, MAX_LENGTH));
//                } else {
//                    request.setMessage(message);
//                }
//                alarmMiddleProxy.sendCompanyWx(request);
//            }
//            throw e;
//        }
//    }
//
//}
