package com.touchbiz.log.starter.alarm;

import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

public interface AlarmServiceOpenApi {
    /**
     * 微信预警
     * @param request
     * @return
     */
    @RequestLine("POST /sys/message/sendCompanyWx")
    String sendCompanyWx(@RequestBody AlarmWechatRequest request);
}
