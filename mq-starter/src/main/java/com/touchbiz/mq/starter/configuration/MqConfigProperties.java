package com.touchbiz.mq.starter.configuration;

import lombok.Data;

/**
 * * dev环境开启swagger文档【启用swagger之前，请配置如下三个变量swagger.enable,swagger.title,swagger.basePackage】
 *
 * @Author: Steven Jiang(mrjiangyan@hotmail.com)
 * @Date: 2018/9/28 12：00
 */
@Data
public class MqConfigProperties {

    /**
     * 此处的type必须和MqTopicTypeEnum中对应
     */
    private String type;

    private String topic;

    private String tag;

    private Integer consumeThreadNums;

}