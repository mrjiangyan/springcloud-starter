//package com.touchbiz.log.starter.alarm;
//
//
//import com.touchbiz.common.entity.result.ApiResult;
//import com.touchbiz.common.entity.result.IResultMsg;
//import com.touchbiz.common.utils.tools.JsonUtils;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
///**
// * @author zhangxuezhen
// * @description: TODO
// * @date 2020/12/101:29 下午
// */
//@Slf4j
//@Service
//public class AlarmMiddleProxy extends BaseMiddleProxy  {
//
//    /**
//     * 发企业微信预警
//     *
//     * @param
//     * @return
//     */
//    public ApiResult sendCompanyWx(AlarmWechatRequest request) {
//        try{
//            log.info("发企业微信预警request{}", request);
//            AlarmServiceOpenApi messageSendService = generatorFeignClient(AlarmServiceOpenApi.class);
//            log.info("发企业微信预警requestJson{}", JsonUtils.toJson(request));
//            String rep = messageSendService.sendCompanyWx(request);
//            log.info("发企业微信预警response{}", rep);
//            CommonResult result = JsonUtils.toObject(rep,CommonResult.class);
//            if ("200".equals(result.getRet())) {
//                return ApiResult.getSuccessResponse(result.getData());
//            }
//        }catch (Exception e){
//            log.error("AlarmMiddleProxy.sendCompanyWx "+e);
//        }
//        return ApiResult.getCustomResponse(IResultMsg.APIEnum.FAILED);
//    }
//
//    @Data
//    public static class CommonResult{
//
//        private String ret;
//
//        private String data;
//    }
//
//
//}
