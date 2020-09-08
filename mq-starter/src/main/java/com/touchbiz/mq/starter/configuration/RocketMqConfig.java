package com.touchbiz.mq.starter.configuration;

import lombok.Data;

import java.util.List;

@Data
public class RocketMqConfig {

    private String accessKey;

    private String secretKey;

    private String groupId;

    private String namedAddress;

    /**
     * 配置消息发送的生产者
     */
    private List<MqConfigProperties> topic;


}
