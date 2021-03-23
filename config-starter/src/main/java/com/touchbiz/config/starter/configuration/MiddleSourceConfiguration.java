package com.touchbiz.config.starter.configuration;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author steven
 */
@Slf4j
@Configuration
public class MiddleSourceConfiguration {

    private final String GROUP_ID = "DEFAULT_GROUP";

    /**
     * 对配置进行加解密处理
     */
    private final String DATA_ID = "MIDDLE_PROXY_CONFIG";

    @Autowired
    private NacosConfigProperties configService;

    @Bean
    public MiddleSourceConfig config() throws NacosException {
        String configInfo = configService.configServiceInstance().getConfig(DATA_ID, GROUP_ID, 3000);

        if(StringUtils.isEmpty(configInfo)){
            throw new RuntimeException("中台调用参数没有配置,dataId: DATA_ID{}" + DATA_ID);
        }

        return JsonUtils.toObject(configInfo , MiddleSourceConfig.class);
    }

    @Data
    public static class MiddleSourceConfig{

        private String middleHost;

        private String middleKey;

        private String middleSecret;

        protected String callbackDomainName;
    }

}