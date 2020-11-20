package com.touchbiz.config.starter;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;


/**
 * * dev环境开启swagger文档【启用swagger之前，请配置如下三个变量swagger.enable,swagger.title,swagger.basePackage】
 *
 * @Author: Steven Jiang(jiangyan@touchbiz-cars.com)
 * @Date: 2018/9/28 12：00
 */
@Configuration
@ConditionalOnProperty(prefix = "swagger", name = "enable", havingValue = "true")
@ConfigurationProperties(prefix = "swagger")
@EnableOpenApi
@Data
public class Swagger3Config {
    /**
     * swagger的标题
     */
    private String title;

    /**
     * 制定訪問host
     */
    private String host;

    @Bean
    public Docket createRestApiBySwagger() {
        Docket docket = new Docket(DocumentationType.OAS_30);
        return docket.apiInfo(new ApiInfoBuilder().title(title).version("1.0").build())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
//                .securityContexts(securityContexts());
    }


    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }

}