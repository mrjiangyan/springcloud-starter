package com.touchbiz.security.starter;

import lombok.Data;


/**
 * @author steven
 */
@Data
public class OSSConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucket;

    private String prefixurl;

    private String securityBucket;

    private String prefix;

    private String process;
}
