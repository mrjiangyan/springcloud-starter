package com.touchbiz.starter.example;


import com.touchbiz.webflux.starter.controller.BaseController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@EnableCaching
@EnableAspectJAutoProxy
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "com.touchbiz.starter.example",
        "com.touchbiz.webflux.starter",
        "com.touchbiz.cache.starter",
        "com.touchbiz.config.starter",
        "com.alibaba.cloud.nacos",
//        "com.touchbiz.db.starter",
})
public class WebFluxExampleApplication extends BaseController {

//    @Qualifier("redisLettuceTemplate")
//    @Autowired
//    private IRedisTemplate redisTemplate;

//    @Qualifier("redisLettuceTemplate2")
//    @Autowired
//    private IRedisTemplate redisTemplate2;

    public static void main(String[] args) {
        SpringApplication.run(WebFluxExampleApplication.class, args);
    }




//    @Bean
//    public ObjectMapper serializingObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        objectMapper.registerModule(new JavaTimeModule());
//        return objectMapper;
//    }

//    @Bean
//    public Formatter<LocalDateTime> localDateTimeFormatter() {
//        return new Formatter<LocalDateTime>() {
//            @Override
//            public String print(LocalDateTime object, Locale locale) {
//                return object.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            }
//
//            @Override
//            public LocalDateTime parse(String text, Locale locale) throws ParseException {
//                return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            }
//        };
//    }


}
