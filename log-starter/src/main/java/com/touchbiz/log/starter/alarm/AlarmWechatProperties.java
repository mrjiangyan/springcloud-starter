package com.touchbiz.log.starter.alarm;


import java.util.Arrays;
import java.util.List;

/**
 * @author zhangxuezhen
 * @description: 企业微信预警，配置文件
 * @date 2020/12/259:46 上午
 */
public class AlarmWechatProperties {


    public static final String WECHAT_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";
    /**
     * 企业微信预警PAT群
     */
    public static final String patWechat = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=664bb6c0-6035-4797-bbe1-b4eafbfd9824";

    /**
     * 企业微信预警UAT群
     */
    public static final String uatWechat = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=54e41e22-8af0-4d7a-b7f1-d8d488e021fc";

    /**
     * 预警组
     */
    public static List<String> warnGroup = List.of();
    /**
     * 黑猫预警组
     */
    public static List<String> blackCatGroup = List.of();
    /**
     * 黄鹰预警组
     */
    public static List<String> yellowEagle = List.of();
    /**
     * 绿草预警组
     */
    public static List<String> greenGrass = List.of();


}
