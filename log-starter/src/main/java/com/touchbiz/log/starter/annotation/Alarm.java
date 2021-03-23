package com.touchbiz.log.starter.annotation;

import java.lang.annotation.*;

/**
 * @author steven
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Alarm {

    /**
     * 自定义群消息机器人webhook地址
     * @return
     */
    String webhook() default "";

    /**
     * 描述
     * @return
     */
    String message() default "";

    /**
     * 自定义通知人，多个用逗号隔开 "@all,17854219747"
     * @return
     */
    String mentionedMobile() default "";

    /**
     * 暂时未打通 通知人预警组 warnGroup、blackCatGroup、yellowEagle、greenGrass
     * @return
     */
    String mobileGroup() default "";


}