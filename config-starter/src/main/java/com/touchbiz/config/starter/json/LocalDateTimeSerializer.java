//package com.touchbiz.config.starter.json;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//
////时间序列化时变为时间戳
//public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
//
//    private String dataFormat;
//
//    protected LocalDateTimeSerializer(Class<LocalDateTime> t, String dateFormat) {
//        super(t);
//        this.dataFormat = dateFormat;
//    }
//
//
//    @Override
//    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//        jsonGenerator.writeNumber(localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
//    }
//}