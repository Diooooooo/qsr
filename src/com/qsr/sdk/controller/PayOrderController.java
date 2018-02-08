package com.qsr.sdk.controller;

import com.qsr.sdk.component.payment.PaymentOrder;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
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
    private static final String[] LEVELS = {"0", "1", "2", "3", "4"};
    private static final String[] PAY_PROVIDER = {"Wxpay", "Alipay", "Accountpay", "Creditpay"};

    public PayOrderController() {
        super(logger);
    }

    public void payOrderRequest() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String typeId = f.s("type_id");
            int fee = f.i("fee", 0);
            String provider = f.s("provider", PAY_PROVIDER[0]);

            if (!Arrays.asList(LEVELS).contains(typeId)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "支付档位不正确");
            }

            if (!Arrays.asList(PAY_PROVIDER).contains(provider)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "支付方式不正确");
            }

            if ("0".equalsIgnoreCase(typeId) && fee < 1) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "自定义金额不能少于1匠币");
            }
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);

            PayOrderService payOrderService = this.getService(PayOrderService.class);
            PaymentOrder paymentOrder = payOrderService.payOrderRequst(userId, typeId, fee, provider,
                    getRealRemoteAddr(), MapUtil.convertMap2(f.getParameters()));
            Map<String, Object> info = new HashMap<>();
            info.put("orderNumber", paymentOrder.getOrderNumber());
            this.renderData(info, SUCCESS);
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
