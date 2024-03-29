package com.touchbiz.webflux.starter.configuration.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FeignConfiguration implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Object user = ReactiveRequestContextHolder.getUser();
        if(user != null){
            template.header(HttpHeaderConstants.HEADER_USER, URLEncoder.encode(JsonUtils.toJson(user), StandardCharsets.UTF_8));
        }
        String tenantId = ReactiveRequestContextHolder.getTenantId();
        if(tenantId != null){
            template.header(HttpHeaderConstants.HEADER_TENANT_ID, tenantId);
        }
        log.info("headers:{}", template.headers());
    }

//    @Bean
//    @Scope("prototype")
//    public Feign.Builder feignBuilder(Retryer retryer) {
//        return Feign.builder().retryer(retryer);
//    }
//
//    @Bean
//    public Decoder feignDecoder() {
//        return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
//    }
//
//    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
//        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(new GateWayMappingJackson2HttpMessageConverter());
//        return () -> httpMessageConverters;
//    }
//
//    public static class GateWayMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
//        GateWayMappingJackson2HttpMessageConverter(){
//            List<MediaType> mediaTypes = new ArrayList<>();
//            mediaTypes.add(MediaType.APPLICATION_JSON);
//            mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//            mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
//            setSupportedMediaTypes(mediaTypes);
//        }
//    }
//
//    @Bean
//    public Encoder feignEncoder() {
//        return new SpringEncoder(feignHttpMessageConverter());
//    }

//    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
//        MappingJackson2HttpMessageConverter jacksonConverter =
//                new MappingJackson2HttpMessageConverter(new ObjectMapper());
//        List<MediaType> unmodifiedMediaTypeList =  jacksonConverter.getSupportedMediaTypes();
//        List<MediaType> mediaTypeList =  new ArrayList<>(unmodifiedMediaTypeList.size()+1);
//        mediaTypeList.addAll(unmodifiedMediaTypeList);
//        mediaTypeList.add(MediaType.APPLICATION_JSON);
//        mediaTypeList.add(MediaType.APPLICATION_OCTET_STREAM);
//        jacksonConverter.setSupportedMediaTypes(mediaTypeList);
//        ObjectFactory<HttpMessageConverters> factory = () -> new HttpMessageConverters(jacksonConverter);
//        return factory;
//    }


    @Bean
    @Primary
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    public Decoder feignDecoder(ObjectMapper objectMapper) {
        return new ResponseEntityDecoder(new SpringDecoder(messageConverters(objectMapper)));
    }

    public ObjectFactory<HttpMessageConverters> messageConverters(ObjectMapper objectMapper) {
        return () -> new HttpMessageConverters(mappingJackson2HttpMessageConverter(objectMapper));
    }

    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }


}
