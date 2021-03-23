package com.touchbiz.webflux.starter.configuration.feign;

import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class FeignConfiguration implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate template) {
        Object user = ReactiveRequestContextHolder.getUser();
        if(user != null){
            template.header(HttpHeaderConstants.HEADER_USER, URLEncoder.encode(JsonUtils.toJson(user)));
        }
        Integer channelId = ReactiveRequestContextHolder.getChannelId();
        if(channelId != null){
            template.header(HttpHeaderConstants.HEADER_CHANNEL, URLEncoder.encode(String.valueOf(channelId)));
        }
        log.info("headers:{}",template.headers());
    }

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder(Retryer retryer) {
        return Feign.builder().retryer(retryer);
    }

    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
    }

    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(new GateWayMappingJackson2HttpMessageConverter());
        return () -> httpMessageConverters;
    }

    public static class GateWayMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        GateWayMappingJackson2HttpMessageConverter(){
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.APPLICATION_JSON);
            mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
            setSupportedMediaTypes(mediaTypes);
        }
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(feignHttpMessageConverter());
    }

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

}
