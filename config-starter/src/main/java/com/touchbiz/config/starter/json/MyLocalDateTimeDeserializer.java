package com.touchbiz.config.starter.json;//package com.touchbiz.config.starter.json;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonToken;
//import com.fasterxml.jackson.core.JsonTokenId;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//
//import java.io.IOException;
//import java.time.DateTimeException;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.time.format.DateTimeFormatter;
//
//public class MyLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
//
//    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//
//
//    public MyLocalDateTimeDeserializer(DateTimeFormatter formatter) {
//        super(formatter);
//    }
//
//    protected MyLocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency) {
//        super(base, leniency);
//    }
//
//    //这里只是简单的根据前端传过来的日期字符串进行简单的处理，然后再进行类型转换
//    //这段代码中有很多漏洞，只是针对常用格式做了简单处理，请慎用！或自己做更全面的考虑并相应的修改！（只是提供了这样一种解决思路）
//    @Override
//    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
//        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
//            //yyyy/MM/dd替换为yyyy-MM-dd
//            String string = parser.getText().trim().replace("/", "-");
//            if (string.length() == 0) {
//                return null;
//            }
//            try {
//                if (this._formatter == DEFAULT_FORMATTER && string.length() > 10 && string.charAt(10) == 'T') {
//                    return string.endsWith("Z") ? LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC) : LocalDateTime.parse(string, DEFAULT_FORMATTER);
//                } else if (string.length() > 10 && string.charAt(10) == 'T') { //处理yyyy-MM-ddTHH:mm:ss.sssZ的格式
//                    return string.endsWith("Z") ? LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC) : LocalDateTime.parse(string, DEFAULT_FORMATTER);
//                } else if (string.length() == 10) {//处理yyyy-MM-dd的格式
//                    return LocalDateTime.parse(string + " 00:00:00", this._formatter);
//                } else {//配置第三步的时候，设置了时间格式为：yyyy-MM-dd HH:mm:ss
//                    return LocalDateTime.parse(string, this._formatter);
//                }
//            } catch (DateTimeException var12) {
//                return this._handleDateTimeException(context, var12, string);
//            }
//        }
//        return super.deserialize(parser,context);
//    }
//}
