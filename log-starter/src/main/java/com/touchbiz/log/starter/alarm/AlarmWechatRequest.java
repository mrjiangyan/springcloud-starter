package com.touchbiz.log.starter.alarm;


import lombok.Data;

import java.util.List;

/**
 * @author zhangxuezhen
 * @description: TODO
 * @date 2020/12/101:11 下午
 */
@Data
public class AlarmWechatRequest {
    /**
     * 当消息类型为news时生效
     */
    private ArticlesDTO articles;
    /**
     * 当消息类型为image时必填，图片内容的base64编码
     */
    private String base64;
    /**
     * 回调 URL，通知发送状态
     */
    private String callbackUrl;
    /**
     * 当消息类型为image时必填，图片内容（base64编码前）的md5值
     */
    private String md5;
    /**
     * 当消息类型为text时生效，账号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
     */
    private String mentionedList;
    /**
     * 当消息类型为text时生效，手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
     */
    private List<String> mentionedMobileList;
    /**
     * 当消息类型为text时内容最长不超过2048个字节（必填），当类型为markdown时内容最长不超过4096个字节，必须是utf8编码（必填）
     */
    private String message;
    /**
     * 消息类型（text:文本类型，markdown:markdown类型,news:图文类型,image:图片类型）必填
     */
    private String msgtype;
    /**
     * 群消息机器人webhook地址 必填
     */
    private String webhook;

    @Data
    public static class ArticlesDTO {
        /**
         * 描述，不超过512个字节，超过会自动截断
         */
        private String description;
        /**
         * 图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图 1068*455，小图150*150
         */
        private String picurl;
        /**
         * 当类型为news时必填，不超过128个字节，超过会自动截断
         */
        private String title;
        /**
         * 当类型为news时必填，点击后跳转的链接
         */
        private String url;
    }
}

