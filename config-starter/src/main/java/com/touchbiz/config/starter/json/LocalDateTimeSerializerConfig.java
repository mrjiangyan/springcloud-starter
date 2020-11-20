package com.touchbiz.config.starter.json;//package com.touchbiz.config.starter.json;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//
//@Configuration
//@ConditionalOnClass({ Jackson2ObjectMapperBuilder.class, LocalDateTime.class })
//public class LocalDateTimeSerializerConfig {
//
//    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
//    private String pattern;
//
//    // 方案三
//    @Bean
//    @Primary
//    public ObjectMapper serializingObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
////       LocalDateTimeSerializer javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
//        objectMapper.registerModule(javaTimeModule);
//        return objectMapper;
//    }
//
//    public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
//        @Override
//        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
//                throws IOException {
//            gen.writeString(value.format(DateTimeFormatter.ofPattern(pattern)));
//        }
//    }
//
//    public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
//        @Override
//        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)
//                throws IOException {
//            return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern(pattern));
//        }
//    }
//
////    @Bean
////    public ObjectMapper serializingObjectMapper() {
////        JavaTimeModule module = new JavaTimeModule();
////        MyLocalDateTimeDeserializer dateTimeDeserializer = new MyLocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
////        module.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
////        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(LocalDateTime.class,"yyyy-MM-dd HH:mm:ss"));
////        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
////        module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
//////        return Jackson2ObjectMapperBuilder.json().modules(module)
//////                .build();
////        ObjectMapper om = new ObjectMapper();
////        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
////        om.registerModule(module);
////        return om;
////    }
//
//
//}