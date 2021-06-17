package com.touchbiz.config.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author jamesluozhiwei
 * @date 2019/12/18
 */
@Configuration
public class LocalDateTimeSerializerConfig {

    @Bean
    WebFluxConfigurer webFluxConfigurer(Jackson2JsonEncoder encoder, Jackson2JsonDecoder decoder){
        return new WebFluxConfigurer() {
            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.defaultCodecs().jackson2JsonEncoder(encoder);
                configurer.defaultCodecs().jackson2JsonDecoder(decoder);
            }
        };

    }

    @Bean
    Jackson2JsonEncoder jackson2JsonEncoder(){
        return new Jackson2JsonEncoder(serializingObjectMapper());
    }

    @Bean
    Jackson2JsonDecoder jackson2JsonDecoder(){
        return new Jackson2JsonDecoder(serializingObjectMapper());
    }

    /**
     * 序列化LocalDateTime
     * @return
     */
    @Bean
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    /**
     * 序列化实现
     */
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {

            if (value != null){

                long timestamp = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }

    /**
     * 反序列化实现
     */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)
                throws IOException {
            long timestamp = p.getValueAsLong();
            if (timestamp > 0){
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.systemDefault());
            }else{
                return null;
            }
        }
    }
}
