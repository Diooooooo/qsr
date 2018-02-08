package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentOrder;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.PaymentHelper;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayOrderService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(PayOrderService.class);
    private static final String SELECT_LEVELS = "SELECT l.level_id, l.level_count " +
            "FROM qsr_payorder_request_level l WHERE l.enabled = 1 ORDER BY l.level_count ASC";
    private static final String SELECT_PROVIDERS = "SELECT p.provider_id, p.provider_en " +
            "FROM qsr_payorder_provider p WHERE p.enabled = 1 ORDER BY p.sorted DESC";
    private static final String SELECT_PROVIDER_WITH_PROVIDER_EN = "SELECT provider_config_id FROM qsr_payorder_provider WHERE provider_en = ? AND enabled = 1 ";
    private static final String NOTIFY_URL = "http://www.liangqiujiang.com/api/notify/notify";
    private static final String PAY_ORDER_INSTANCE = "INSERT INTO qsr_payorder_request(order_number,request_date,user_id," +
            "request_provider_id,payee_account,level_id,fee,currency_amount,currency_type_id " +
            " ,currency_used,status_id,client_ip,sign_type,original_body,original_detail,original_data " +
            "SELECT i.orderNumber, NOW(), i.user_id, p.provider_id, '', l.level_id, i.fee, i.currency_amount, " +
            "  i.currency_type_id, i.currency_amount, 2, i.clientIp, i.sign_type, i.original_body, i.original_detail, i.original_data " +
            "  FROM (SELECT ? AS orderNumber, ? AS level_en, ? AS provider_en, ? AS currency_type_id, ? AS user_id, " +
            "? AS fee, ? AS currency_amount, ? AS clientIp, ? AS sign_type, ? AS original_body, " +
            "? AS original_detail, ? AS original_data) i " +
            "INNER JOIN qsr_payorder_request_level l ON l.level_en = i.level_en " +
            "INNER JOIN qsr_payorder_request_provider p ON p.provider_en = i.provider_en " +
            "INNER JOIN qsr_user_currency_type t ON t.currency_type_id = i.currency_type_id";

    public List<Map<String,Object>> getPayOrderLevel() throws ServiceException {
        try {
            List<Map<String, Object>> levels = record2list(Db.find(SELECT_LEVELS));
            Map<String, Object> custom = new HashMap<>();
            custom.put("level_id", "0");
            custom.put("level_count", 100);
            levels.add(custom);
            return levels;
        } catch (Throwable t) {
            logger.error("getPayOrderLevel was error, exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "充值档位加载失败", t);
        }
    }

    public List<Map<String,Object>> getPayOrderProvider() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_PROVIDERS));
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "充值类型加载失败", t);
        }
    }

    public PaymentOrder payOrderRequst(int userId, String typeId, int fee, String provider, String clientIP, Map<String, Object> req) throws ServiceException {
        try {
            Map<String, Object> providerInfo = record2map(Db.findFirst(SELECT_PROVIDER_WITH_PROVIDER_EN, provider));
            if (null == providerInfo) {
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "不存在的支付服务");
            }
            Parameter p = new Parameter(providerInfo);
            Payment payment = PaymentHelper.getPayment(p.i("provider_config_id"));
            final PaymentOrder[] order = {null};
            boolean provider_config_id = Db.tx(() -> {
                try {
                    String origParams = MapUtil.serialize(req, Env.getCharset());
                    order[0] = payment.request(p.s("provider_config_id"), fee, clientIP, req, NOTIFY_URL);
                    return null != order[0] && (Db.update(PAY_ORDER_INSTANCE, order[0].getOrderNumber(),
                            typeId, req.get("provider_en"), req.get("currency_type_id"), userId,
                            req.get("fee"), req.get("currency_amount"), clientIP, req.get("sign_type"),
                            req.get("original_body"), req.get("original_detail"), origParams) > 0);
                } catch (PaymentException e) {
                    logger.error("payOrderRequest was error. exception = {} ", e);
                    return false;
                }
            });
            if (!provider_config_id)
                return order[0];
            else
                throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "订单创建失败");
        } catch (Throwable t) {
            logger.error("payOrderRequest was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "创建订单失败", t);
        }
    }
}
