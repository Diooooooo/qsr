package com.qsr.sdk.controller;

import com.jfinal.kit.HttpKit;
import com.qsr.sdk.component.payment.provider.weixin.WeixinPayment;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.PayOrderService;
import com.qsr.sdk.util.*;
import com.qsr.sdk.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PayOrderNotifyController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(PayOrderNotifyController.class);
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";

    public PayOrderNotifyController() {
        super(logger);
    }

    public void payNotify() {
        try {
            String res = HttpKit.readIncommingRequestData(this.getRequest());
            logger.debug("payNotify response = {} ", res);
            Map<String, String> resMap = getResponseMap(res);
            // 通讯结果
            if (SUCCESS.equals(resMap.get("return_code").toUpperCase())) {
                Parameter p = new Parameter(resMap);
                PayOrderService payOrderService = this.getService(PayOrderService.class);
                String outTradeNo = p.s("out_trade_no");
                String resultCode = p.s("result_code").toUpperCase();
                String resultMsg = p.s("err_code_des", StringUtil.NULL_STRING);
                String provider = "Wxpay";
                if (payOrderService.isDisposed(outTradeNo)) {
                    payOrderService.saveNotifyRemote(outTradeNo, provider, resultCode, resultMsg, p.toString(), res);
                    // 交易成功
                    if (SUCCESS.equals(resultCode)) {
                        String sign = resMap.remove("sign");
                        String openid = resMap.get("openid");
                        String resSign = WeixinPayment.resSign(resMap);
                        int statusId = 4;
                        Map<String, Object> providerInfo = payOrderService.getProviderInfo(provider);
                        if (null == providerInfo) {
                            throw new ApiException(ErrorCode.INTERNAL_EXCEPTION, "暂不支持的支付服务");
                        }
                        //校验签名
                        if (sign.equals(resSign)) {
                            //更改订单
                            Map<String, Object> info = payOrderService.getPayRequestInfo(outTradeNo);
                            payOrderService.modifyPayOrderWithNotify(outTradeNo,
                                    (Integer) providerInfo.get("provider_id"), resultCode, statusId, openid, (Integer) info.get("user_id"));
                        } else {
                            logger.error("有人试图篡改支付数据，已被系统拦截并记录。remote addr = {}, user agent = {}",
                                    this.getRealRemoteAddr(), this.getUserAgent());
                        }
                    } else {
                        // 交易失败
                        String errCode = p.s("err_code");
                        String errCodeDes = p.s("err_code_des");
                    }
                }
                this.renderData();
            } else {
                throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "通信失败");
            }
        } catch (Throwable t) {
            logger.error("notify was error. exception = {}", t);
            this.renderException("payNotify", t);
        }
    }

    public void refund() {
        try {
            String res = HttpKit.readIncommingRequestData(this.getRequest());
            if (StringUtil.NULL_STRING == res) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不完整");
            }
            logger.debug("refund response = {} ", res);
            Map<String, String> resMap = getResponseMap(res);
            // 通讯结果
            if (SUCCESS.equals(resMap.get("return_code").toUpperCase())) {
                Parameter p = new Parameter(resMap);
                PayOrderService payOrderService = this.getService(PayOrderService.class);
                String reqInfo = p.s("req_info");
                Map<String, String> decodeMap = WeixinPayment.decodeRefund(reqInfo);
                String resultCode = resMap.get("return_code");
                String resultMsg = decodeMap.get("result_msg");
                String outTradeNo = decodeMap.get("out_trade_no");
                String provider = "Wxpay";
                if (payOrderService.isDisposedRefund(outTradeNo)) {
                    payOrderService.saveRefundNotify(outTradeNo, provider, resultCode, resultMsg, p.toString(), res);
                    // 交易成功
                    if (SUCCESS.equals(resultCode)) {
//                        String sign = resMap.remove("sign");
                        String openid = resMap.get("openid");
//                        String resSign = WeixinPayment.resSign(resMap);
                        Map<String, Object> providerInfo = payOrderService.getProviderInfo(provider);
//                        if (null == providerInfo) {
//                            throw new ApiException(ErrorCode.INTERNAL_EXCEPTION, "暂不支持的支付服务");
//                        }
                        //校验签名
//                        if (sign.equals(resSign)) {
                            //更改订单
                            Map<String, Object> info = payOrderService.getPayRequestInfo(outTradeNo);
                            payOrderService.refundNotify(outTradeNo, (Integer) providerInfo.get("provider_id"),
                                    resultCode, openid, (Integer) info.get("user_id"));
//                        } else {
//                            logger.error("有人试图篡改退款数据，已被系统拦截并记录。remote addr = {}, user agent = {}",
//                                    this.getRealRemoteAddr(), this.getUserAgent());
//                        }
                    } else {
                        // 交易失败
                        String errCode = p.s("err_code");
                        String errCodeDes = p.s("err_code_des");
                    }
                }
                this.renderData();
            } else {
                throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "通信失败");
            }
        } catch (Throwable t) {
            logger.error("refund was error. exception = {} ", t);
            this.renderException("refund", t);
        }
    }

    private Map<String, String> getResponseMap(String res) throws ApiException {
        try {
            if (StringUtil.NULL_STRING == res) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不完整");
            }
            return XmlUtil.xml2map(res);
        } catch (Throwable t) {
            logger.error("getREsponseMap was error. exception = {} ", t);
            throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不完整");
        }
    }

}
