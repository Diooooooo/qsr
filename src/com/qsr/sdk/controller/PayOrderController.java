package com.qsr.sdk.controller;

import com.qsr.sdk.component.payment.PaymentOrder;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.EsotericaService;
import com.qsr.sdk.service.PayOrderService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.ErrorCode;
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
    private static final String[] TYPES = {"A", "O", "W"};

    public PayOrderController() {
        super(logger);
    }

    public void payOrderRequest() {
        try {
            Fetcher f = this.fetch();
            logger.debug("payOrderRequest params = {}", f);
            String sessionkey = f.s("sessionkey");
            String typeId = f.s("type_id");
            String platform = f.s("platform", "Android");
//            int fee = f.i("fee", 0);
            String provider = f.s("provider", PAY_PROVIDER[0]);

//            if (!Arrays.asList(LEVELS).contains(typeId)) {
//                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "暂不支持自定义金额充值");
//            }

            if (!Arrays.asList(PAY_PROVIDER).contains(provider)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "支付方式不正确");
            }

            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);

            PayOrderService payOrderService = this.getService(PayOrderService.class);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            Parameter levelInfo = new Parameter(esotericaService.getEsotericaInfo(typeId));
            Map<String, Object> params = new HashMap<>();
            if (null == levelInfo) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不合法");
            } else {
                params.put("fee", levelInfo.i("price"));
                params.put("currency_amount", levelInfo.i("price"));
                params.put("type_id", typeId);
                params.put("currency_type_id", levelInfo.i("type_id"));
                params.put("purchase_Name", "购买锦囊");
                params.put("esoterica_no", typeId);
                params.put("level_en", StringUtil.NULL_STRING);
            }
            params.put("provider", provider);
            params.put("sign_type", "MD5");
            params.put("platform", platform);
            PaymentOrder paymentOrder = payOrderService.payOrderRequst(userId, levelInfo.i("price"), provider,
                    getRealRemoteAddr(), params);
            Map<String, String> info = paymentOrder.getConf();
            info.remove("detail");
            info.remove("body");
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

    public void getPayOrderList() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String type = f.s("type", "A");
            logger.debug("getPayOrderList params = {}", f);
            int pageNumber = f.i("pageNumber", DEFAULT_PAGE_NUMBER);
            int pageSize = f.i("pageSize", DEFAULT_PAGE_SIZE);
            if (!Arrays.asList(TYPES).contains(type)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不正确");
            }
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            PageList<Map<String, Object>> pay = payOrderService.getPayOrderList(pageNumber, pageSize, userId, type);
            this.renderData(pay, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getPayOrderList", t);
        }
    }

    public void rePay() {
        try {
            Fetcher f = this.fetch();
            String orderNumber = f.s("order_number");
            String sessionkey = f.s("sessionkey");
            String provider = f.s("provider", PAY_PROVIDER[0]);
            String platform = f.s("platform", StringUtil.NULL_STRING);
            if (!Arrays.asList(PAY_PROVIDER).contains(provider)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "支付方式不正确");
            }
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            Map<String, Object> m = new HashMap<>();
            m.put("platform", platform);
            PaymentOrder paymentOrder = payOrderService.rePayOrderRequest(userId, orderNumber, provider,
                    getRealRemoteAddr(), m);
            this.renderData(paymentOrder.getConf(), SUCCESS);
        } catch (Throwable t) {
            this.renderException("rePay", t);
        }
    }

    public void cancelPayOrder() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String requestId = f.s("order_number");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            payOrderService.cancelPayOrder(userId, requestId);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("cancelPayOrder", t);
        }
    }

    public void delPayOrder() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String requestId = f.s("order_number");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            payOrderService.delPayOrder(userId, requestId);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("delPayOrder", t);
        }
    }

    public void payRequestV2() {
        try {
            Fetcher f  = this.fetch();
            String sessionkey = f.s("sessionkey");
            String esotericaNo = f.s("esotericaNo");
            String provider = f.s("provider");
            String platform = f.s("platform", "Android");
            if (!StringUtil.isEmptyOrNull(sessionkey) || !StringUtil.isEmptyOrNull(esotericaNo) || !StringUtil.isEmptyOrNull(provider)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不可为空");
            }
            if (!Arrays.asList(PAY_PROVIDER).contains(provider)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "暂不支持的支付方式");
            }
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            Map<String, Object> info = esotericaService.getEsotericaInfo(esotericaNo);
            if (null == info) {
                throw new ApiException(ErrorCode.PRODUCT_NOT_EXIST, "参数错误");
            }
            Map<String, Object> params = new HashMap<>();
            params.put("fee", info.get("price"));
            params.put("currency_amount", info.get("price"));
            params.put("type_id", info.get("esoterica_no"));
            params.put("currency_type_id", info.get("type_id"));
            params.put("purchase_Name", info.get("title"));
            params.put("level_en", info.get("title"));
            params.put("provider", provider);
            params.put("sign_type", "MD5");
            params.put("platform", platform);
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            PaymentOrder paymentOrder = payOrderService.payRequestV2(userId, (Integer) info.get("price"), provider,
                    getRealRemoteAddr(), params);
            if (null == paymentOrder) {
                throw new ApiException(ErrorCode.INTERNAL_EXCEPTION, "申请退款错误，请稍后再试");
            } else {
                this.renderData(SUCCESS);
            }
        } catch (Throwable t) {
            this.renderException("payRequestV2", t);
        }
    }

    public void refund() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String orderNo = f.s("orderNo");
            logger.debug("refund was params = {} ", f);
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            PayOrderService payOrderService = this.getService(PayOrderService.class);
            if (!payOrderService.isDisposedOrder(userId, orderNo)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不正确");
            }
            payOrderService.refund(userId, orderNo, getRealRemoteAddr());
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("refund", t);
        }
    }
}
