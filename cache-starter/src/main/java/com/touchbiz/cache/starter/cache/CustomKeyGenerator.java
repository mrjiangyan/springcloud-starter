//package com.touchbiz.cache.starter.cache;
//
//import org.springframework.cache.interceptor.KeyGenerator;
//
//import java.lang.reflect.Method;
//import org.springframework.util.ObjectUtils;
//
//public class CustomKeyGenerator implements KeyGenerator {
//
//    //Object 调用方法的对象
//    //Method 调用的方法
//    //objects 参数集合
//
//    @Override
//    public Object generate(Object o, Method method, Object... objects) {
//        String key ="";
//        if(!ObjectUtils.isEmpty(objects)){
//            if(objects[0].getClass().isAssignableFrom(UserModel.class)){
//                UserModel userModel = (UserModel)objects[0];
//                key = userModel.getUserName();
//            }
//        }else{
//            throw new IllegalArgumentException("key设置失败");
//        }
//        return key;
//    }
//}
