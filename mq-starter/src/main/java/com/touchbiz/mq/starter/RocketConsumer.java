package com.touchbiz.mq.starter;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.touchbiz.mq.starter.configuration.RocketMqConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @author zhushen
 * 2018/8/1
 * 下午4:29.
 */
@Slf4j
@Data
public class RocketConsumer {

    private RocketMqConfig config;

    private MessageListener messageListener;

    private Consumer consumer;

    private String topic;

    private String tag;

    private String groupId;

    private Integer consumeThreadNums;

    private String topicType;

    private void init() {
        // consumer 实例配置初始化
        Properties properties = new Properties();
        //您在控制台创建的 Group ID
        if (!StringUtils.isEmpty(groupId)) {
            properties.put(PropertyKeyConst.GROUP_ID, groupId);
        }

        if (consumeThreadNums != null) {
            properties.put(PropertyKeyConst.ConsumeThreadNums, consumeThreadNums);
        }
        // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey, config.getAccessKey());
        // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, config.getSecretKey());
        //设置发送超时时间，单位毫秒
        properties.put(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
        // 设置 TCP 接入域名，进入控制台的实例管理页面的“获取接入点信息”区域查看
        properties.put(PropertyKeyConst.NAMESRV_ADDR, config.getNamedAddress());

        consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe(topic, tag, messageListener);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
        consumer.start();
        log.info("RocketConsumer init success,topic name:{},listener:{}", topic, messageListener.getClass().getName());

    }

    private synchronized void close() {
        if (consumer != null) {
            try {
                consumer.shutdown();
            } catch (Exception e) {
                log.error("rocket consumer close exception", e);
            }
        }
    }
}