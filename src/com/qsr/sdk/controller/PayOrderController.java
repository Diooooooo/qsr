package com.qsr.sdk.controller;

import com.qsr.sdk.component.payment.PaymentOrder;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.PayOrderService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.MapUtil;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayOrderController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(PayOrderController.class);
    private static final String[] LEVELS = {"1", "2", "3", "4"};
    private static final String[] PAY_PROVIDER = {"Wxpay", "Alipay", "Accountpay", "Creditpay"};

    public PayOrderController() {
        super(logger);
    }

    public void payOrderRequest() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String typeId = f.s("type_id");
//            int fee = f.i("fee", 0);
            String provider = f.s("provider", PAY_PROVIDER[0]);

            if (!Arrays.asList(LEVELS).contains(typeId)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "暂不支持自定义金额充值");
            }

            if (!Arrays.asList(PAY_PROVIDER).contains(provider)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "支付方式不正确");
            }

            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);

            PayOrderService payOrderService = this.getService(PayOrderService.class);
            Parameter levelInfo = new Parameter(payOrderService.getLevelInfo(typeId));
            Map<String, Object> params = new HashMap<>();
            if (null == levelInfo) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不合法");
            } else {
                params.put("fee", levelInfo.i("level_count"));
                params.put("currency_amount", levelInfo.i("level_count"));
                params.put("type_id", levelInfo.i("level_id"));
                params.put("currency_type_id", levelInfo.i("currency_type_id"));
            }
            params.put("sign_type", "MD5");
            params.put("original_body", "");
            params.put("original_detail", "");
            PaymentOrder paymentOrder = payOrderService.payOrderRequst(userId, typeId, levelInfo.i("level_count"), provider,
                    getRealRemoteAddr(), params);
            this.renderData(paymentOrder.getConf(), SUCCESS);
        } catch (Throwable t) {
            this.renderException("payOrderRequest", t);
        }
    }

    public void getPayOrderLevel() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            if (StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                int userId = userService.getUserIdBySessionKey(sessionkey);
            }
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            List<Map<String, Object>> levels = payOrderService.getPayOrderLevel();
            this.renderData(levels, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getPayOrderLevel", t);
        }
    }

    public void getPayOrderProvider() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            if (StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                int userId = userService.getUserIdBySessionKey(sessionkey);
            }
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            List<Map<String, Object>> providers = payOrderService.getPayOrderProvider();
            this.renderData(providers, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getPayOrderProvider", t);
        }
    }
}
