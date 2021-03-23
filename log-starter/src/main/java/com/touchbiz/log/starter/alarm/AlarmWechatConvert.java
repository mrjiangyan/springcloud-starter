package com.touchbiz.log.starter.alarm;

public class AlarmWechatConvert {

    private AlarmWechatConvert(){

    }

    public static StringBuilder transformOutBuffer(String applicationName) {
        //描述头
        StringBuilder messageBuffer = new StringBuilder();
        messageBuffer.append("[");
        messageBuffer.append(applicationName);
        messageBuffer.append("预警");
        messageBuffer.append("]\n");
        return messageBuffer;
    }

    public static StringBuilder append(StringBuilder buffer, String str, String str1) {
        buffer.append(str);
        buffer.append(": ");
        buffer.append(str1);
        buffer.append("\n");
        return buffer;
    }
}
