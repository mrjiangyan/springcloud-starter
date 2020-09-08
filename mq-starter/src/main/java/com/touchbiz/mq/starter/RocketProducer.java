package com.touchbiz.mq.starter;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import com.touchbiz.common.utils.text.StringSpliceUtils;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.mq.starter.configuration.MqConfigProperties;
import com.touchbiz.mq.starter.configuration.RocketMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author zhushen
 * 2018/8/1
 * 下午4:29.
 */
@Slf4j
public class RocketProducer {

    private static ConcurrentHashMap<String, Pair<MqConfigProperties, Producer>> producerMap;

    private final RocketMqConfig config;

    /**
     * 通过构造函数加载需要加载的MQ消息Topic
     */
    public RocketProducer(RocketMqConfig config) {
        this.config = config;
    }

    /**
     * 同步发送消息
     *
     * @param topicType 消息主题类别
     * @param data      发送的对象，将对象序列化为json字符串之后发送
     * @return
     * @throws Exception
     */
    public static SendResult sendMessage(String topicType, Object data) throws Exception {
        return internalSendMessage(topicType, data, null, pair -> pair.getKey().send(pair.getValue()));
    }

    /**
     * 同步发送消息,支持延迟和定时发送
     *
     * @param topicType 消息主题类别
     * @param data      发送的对象，将对象序列化为json字符串之后发送
     * @return
     * @throws Exception
     */
    public static SendResult sendMessage(String topicType, Object data, Date deliverTime) throws Exception {
        return internalSendMessage(topicType, data, deliverTime, pair -> pair.getKey().send(pair.getValue()));
    }

    /**
     * 异步发送消息
     *
     * @param topicType 消息主题类别
     * @param data      发送的对象，将对象序列化为json字符串之后发送
     * @return
     * @throws Exception
     */
    public static void sendOnewayMessage(String topicType, Object data) throws Exception {
        internalSendMessage(topicType, data, null, pair -> {
            pair.getKey().sendOneway(pair.getValue());
            return null;
        });
    }

    /**
     * 异步发送消息,支持延迟和定时发送
     *
     * @param topicType 消息主题类别
     * @param data      发送的对象，将对象序列化为json字符串之后发送
     * @return
     * @throws Exception
     */
    public static void sendOnewayMessage(String topicType, Object data, Date deliverTime) throws Exception {
        internalSendMessage(topicType, data, deliverTime, pair -> {
            pair.getKey().sendOneway(pair.getValue());
            return null;
        });
    }

    /**
     * 异步发送消息
     *
     * @param topicType 消息主题类别
     * @param data      发送的对象，将对象序列化为json字符串之后发送
     * @return
     * @throws Exception
     */
    public static void sendAsyncMessage(String topicType, Object data, SendCallback callback) throws Exception {
        check(topicType);
        Pair<MqConfigProperties, Producer> pair = producerMap.get(topicType);
        try {
            pair.getValue().sendAsync(generateMessage(topicType, data, null), callback);
        } catch (Exception e) {
            log.info("sendAsyncMessage rocket producer[" + topicType + "]  msg[" + data + "] exception,e=", e);
        }
    }


    /**
     * 异步发送消息,支持延迟和定时发送
     *
     * @param topicType 消息主题类别
     * @param data      发送的对象，将对象序列化为json字符串之后发送
     * @return
     * @throws Exception
     */
    public static void sendAsyncMessage(String topicType, Object data, Date deliverTime, SendCallback callback) throws Exception {
        check(topicType);
        Pair<MqConfigProperties, Producer> pair = producerMap.get(topicType);
        try {
            pair.getValue().sendAsync(generateMessage(topicType, data, deliverTime), callback);
        } catch (Exception e) {
            log.info("sendAsyncMessage rocket producer[" + topicType + "]  msg[" + data + "] exception,e=", e);
        }
    }

    private static SendResult internalSendMessage(String topicType, Object data, Date deliverTime, Function<Pair<Producer, Message>, SendResult> function) throws Exception {
        check(topicType);
        SendResult result;
        Pair<MqConfigProperties, Producer> pair = producerMap.get(topicType);
        try {

            result = function.apply(new MutablePair<>(pair.getValue(), generateMessage(topicType, data, deliverTime)));
            if (result.getMessageId() != null) {
                log.info("sendMessage rocket success. producer={}, msg={},messageId={}", topicType, data, result.getMessageId());
            } else {
                log.info("sendMessage rocket fail. producer={}, msg={}", topicType, data);
            }
        } catch (Exception e) {
            log.info("sendMessage rocket producer[" + topicType + "]  msg[" + data + "] exception,e=", e);
            result = null;
        }
        return result;
    }

    private static void check(String topicType) throws Exception {
        if (CollectionUtils.isEmpty(producerMap)) {
            log.error("sendMessage mafka producerMap has not init");
            throw new Exception("没有任何队列无法发送");
        }
        if (!producerMap.containsKey(topicType) || producerMap.get(topicType) == null) {
            log.error("sendMessage rocket producer={} has not init", topicType);
            throw new Exception(StringSpliceUtils.splice("不存在{}该队列,无法进行发送操作", topicType));
        }
    }

    private static Message generateMessage(String topicType, Object data, Date deliverTime) {
        if (data == null) {
            log.error("sendMessage  rocket producer={}, param o is null", topicType);
            return null;
        }
        String msg;
        if (data instanceof String) {
            msg = (String) data;
        } else if (data instanceof JSONObject) {
            msg = ((JSONObject) data).toJSONString();
        } else {
            msg = JsonUtils.toJson(data);
        }

        if (msg == null) {
            log.error("sendMessage rocket producer={}. msg is null", topicType);
            return null;
        }
        Pair<MqConfigProperties, Producer> pair = producerMap.get(topicType);
        Message message = new Message(pair.getKey().getTopic(), pair.getKey().getTag(), msg.getBytes());

        if (deliverTime != null) {
            message.setStartDeliverTime(deliverTime.getTime());
        }
        return message;
    }

    private void init() {
        if (producerMap != null) {
            return;
        }
        synchronized (RocketProducer.class) {
            log.info("init rocket producerMap,configuration:{}", config);
            producerMap = new ConcurrentHashMap<>(16);
            List<MqConfigProperties> topic = config.getTopic();
            if (topic == null) {
                return;
            }
            for (MqConfigProperties mqConfigProperties : topic) {
                // producer 实例配置初始化
                Properties properties = new Properties();
                //您在控制台创建的 Group ID
                if (!StringUtils.isEmpty(config.getGroupId())) {
                    properties.put(PropertyKeyConst.GROUP_ID, config.getGroupId());
                }
                // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
                properties.put(PropertyKeyConst.AccessKey, config.getAccessKey());
                // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
                properties.put(PropertyKeyConst.SecretKey, config.getSecretKey());
                //设置发送超时时间，单位毫秒
                properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
                // 设置 TCP 接入域名，进入控制台的实例管理页面的“获取接入点信息”区域查看
                properties.put(PropertyKeyConst.NAMESRV_ADDR,
                        config.getNamedAddress());
                final Producer producer = ONSFactory.createProducer(properties);
                // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
                producer.start();
                log.info("rocket producer[" + mqConfigProperties.getTopic() + "] init success");
                Pair<MqConfigProperties, Producer> pair = new ImmutablePair<>(mqConfigProperties, producer);
                producerMap.put(mqConfigProperties.getType(), pair);
            }
        }
    }

    private synchronized void close() {
        if (producerMap == null) {
            return;
        }
        if (producerMap.size() == 0) {
            producerMap = null;
            return;
        }
        for (Pair<MqConfigProperties, Producer> pair : producerMap.values()) {
            Producer processor = pair.getValue();
            if (processor != null) {
                try {
                    processor.shutdown();
                } catch (Exception e) {
                    log.error("rocket producer close exception", e);
                }
            }
        }
        producerMap.clear();
        producerMap = null;
    }
}
