package com.touchbiz.security.starter.configuration;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * * dev环境开启swagger文档【启用swagger之前，请配置如下三个变量swagger.enable,swagger.title,swagger.basePackage】
 *
 * @Author: Steven Jiang(mrjiangyan@hotmail.com)
 * @Date: 2018/9/28 12：00
 */
@Configuration
@ConditionalOnProperty(name = "knife4j.enable",havingValue = "true")
@ConfigurationProperties(prefix = "knife4j")
@Data
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger3Config {
    /**
     * swagger的标题
     */
    private String title;

    @Bean
    public Docket createRestApiBySwagger() {
        Docket docket = new Docket(DocumentationType.OAS_30);
        return docket.pathMapping("/")
                .apiInfo(new ApiInfoBuilder().title(title).version("1.0").build())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }


    @ConditionalOnProperty(name = "knife4j.enable",havingValue = "true")
    @Configuration
    public class SwaggerDocsConfiguration implements WebFluxConfigurer {

        private final String WEBJAR_PATH = "/webjars/**";

        @Autowired
        private ResourceProperties resourceProperties;

        /**
         * 1-配置静态访问资源
         * @param registry
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            if (!registry.hasMappingForPattern(WEBJAR_PATH)) {
                registry.addResourceHandler(WEBJAR_PATH)
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
            String staticPathPattern = "*.html";
            registry.addResourceHandler(staticPathPattern)
                    .addResourceLocations(resourceProperties.getStaticLocations());

        }
    }

}