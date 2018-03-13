package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentOrder;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.PaymentHelper;
import com.qsr.sdk.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.qsr.sdk.util.StringUtil.*;

public class PayOrderService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(PayOrderService.class);
    private static final String SELECT_LEVELS = "SELECT l.level_id, l.level_count/100 level_count " +
            "FROM qsr_payorder_request_level l WHERE l.enabled = 1 ORDER BY l.level_count ASC";
    private static final String SELECT_PROVIDERS = "SELECT p.provider_id, p.provider_en " +
            "FROM qsr_payorder_provider p WHERE p.enabled = 1 ORDER BY p.sorted DESC";
    private static final String SELECT_PROVIDER_WITH_PROVIDER_EN = "SELECT provider_config_id FROM qsr_payorder_provider WHERE provider_en = ? AND enabled = 1 ";
    private static final String NOTIFY_URL = "http://liangqiujiang.com:8080/api/notify/payNotify";
    private static final String PAY_ORDER_INSTANCE = "INSERT INTO qsr_payorder_request(order_number,request_date,user_id," +
            "request_provider_id,payee_account,level_id,fee,currency_amount,currency_type_id " +
            " ,status_id,client_ip,sign_type,original_body,original_detail,original_data, prepay_id, app_type_id) " +
            "SELECT i.orderNumber, NOW(), i.user_id, p.provider_id, null, l.level_id, IF(0 = l.level_count, i.fee, l.level_count), IF(0 = l.level_count, i.currency_amount, l.level_count), " +
            "  t.currency_type_id, 2, i.clientIp, i.sign_type, i.original_body, i.original_detail, i.original_data, i.prepay_id, IF(null = at.type_id, -1, at.type_id) " +
            "  FROM (SELECT ? AS orderNumber, ? AS level_en, ? AS provider_en, ? AS currency_type_id, ? AS user_id, " +
            "? AS fee, ? AS currency_amount, ? AS clientIp, ? AS sign_type, ? AS original_body, " +
            "? AS original_detail, ? AS original_data, ? as prepay_id, ? AS platform) i " +
            "INNER JOIN qsr_payorder_request_level l ON l.level_en = i.level_en " +
            "INNER JOIN qsr_payorder_provider p ON p.provider_en = i.provider_en " +
            "INNER JOIN qsr_user_currency_type t ON t.currency_type_id = i.currency_type_id " +
            "LEFT JOIN qsr_app_type at ON at.type_name = i.platform " +
            "ON DUPLICATE UPDATE KEY request_date = NOW(), request_provider_id = i.provider, level_id = l.level_id, " +
            "fee = IF(0 = l.level_count, i.fee, l.level_count), " +
            "currency_amount = IF(0 = l.level_count, i.currency_amount, l.level_count)," +
            "client_ip = i.clientIp, original_body = i.original_body, original_detail = i.original_detail, original_data = i.original_data, prepay_id = i.prepay_id ";
    private static final String SELECT_PAYORDERS = "SELECT l.income, t.name, l.createtime ";
    private static final String FROM_PAYORDERS = "FROM qsr_user_balance_log l " +
            "INNER JOIN qsr_user_currency_type t ON t.currency_type_id = l.currency_type_id WHERE l.user_id = ? ";
    private static final String PAYORDERS_ORDER_BY = " ORDER BY l.createtime DESC ";
    private static final String[] TYPES = {" AND l.createtime >= NOW() - INTERVAL 1 WEEK",
            " AND l.createtime >= NOW() - INTERVAL 2 WEEK", " AND l.createtime >= NOW() - INTERVAL 1 MONTH",
            " AND l.createtime >= NOW() - INTERVAL 3 MONTH"};
    private static final String SELECT_PAYORDER_WITH_STATUS = "SELECT l.level_count, " +
            "CONCAT('匠币充值[', l.level_count, ']') title, l.level_name, q.order_number, q.fee, " +
            "s.status_id, q.createtime, q.updatetime ";
    private static final String FROM_PAYORDER_WITH_ALL_STATUS = " FROM qsr_payorder_request q " +
            "  INNER JOIN qsr_payorder_request_status s ON q.status_id = s.status_id " +
            "  INNER JOIN qsr_payorder_request_level l ON q.level_id = l.level_id " +
            "  WHERE q.user_id = ? " +
            "  AND q.status_id IN (1, 2, 3, 4, 5, 6) " +
            "  ORDER BY q.updatetime DESC";
    private static final String FROM_PAYORDER_WITH_OVER_STATUS = " FROM qsr_payorder_request q " +
            "  INNER JOIN qsr_payorder_request_status s ON q.status_id = s.status_id " +
            "  INNER JOIN qsr_payorder_request_level l ON q.level_id = l.level_id " +
            "  WHERE q.user_id = ? " +
            "  AND q.status_id = 4 " +
            "  ORDER BY q.updatetime DESC";
    private static final String FROM_PAYORDER_WITH_WAIT_STATUS = " FROM qsr_payorder_request q " +
            "  INNER JOIN qsr_payorder_request_status s ON q.status_id = s.status_id " +
            "  INNER JOIN qsr_payorder_request_level l ON q.level_id = l.level_id " +
            "  WHERE q.user_id = ? " +
            "  AND q.status_id IN (3, 5, 6) " +
            "  ORDER BY q.updatetime DESC";
    private static final String SAVE_PAYORDER_NOTIFY = "INSERT INTO qsr_payorder_result(order_number,provider_id," +
            "return_code,return_message,original_notify, original_body) " +
            "SELECT i.orderNumber, p.provider_id, i.returnCode, i.returnMsg, i.notify, i.body " +
            "FROM (SELECT ? AS orderNumber, ? AS provider, ? AS returnCode, ? AS returnMsg, ? AS notify, ? as body) i " +
            "INNER JOIN qsr_payorder_provider p ON p.provider_en = i.provider AND p.enabled = 1 ";
    private static final String MODITY_PAYORDER = "UPDATE qsr_payorder_request r " +
            "INNER JOIN qsr_payorder_result rs ON r.order_number = rs.order_number " +
            "SET r.status_id = ?, r.payee_account = ?, r.currency_used = 1 " +
            "WHERE rs.order_number = ? AND rs.provider_id = ? AND rs.return_code = ?";
    private static final String PROVIDER_INFO = "SELECT p.provider_id, p.provider_name, p.sorted " +
            "FROM qsr_payorder_provider p WHERE p.provider_en = ? AND p.enabled = 1";
    private static final String SELECT_PAYORDER_REQUEST = "SELECT r.order_number FROM qsr_payorder_request r " +
            "WHERE r.enabled = 1 AND r.order_number = ? AND r.currency_used = 0 AND r.status_id = 2 ";
    private static final String BALANCE_EDCATION = "INSERT INTO qsr_user_balance(user_id,currency_type_id," +
            "total,block,balance) SELECT r.user_id, 3, r.fee, r.fee, r.fee FROM (SELECT ? AS outTradeno) i " +
            "INNER JOIN qsr_payorder_request r ON r.order_number = i.outTradeno AND r.enabled = 1 " +
            "AND r.currency_used = 1 AND r.status_id = 4 " +
            "INNER JOIN qsr_users u ON u.id = r.user_id " +
            "ON DUPLICATE KEY UPDATE total = total + r.fee, balance = balance + r.fee, block = block + r.fee";
    private static final String BALANCE_EDCATION_LOG = "INSERT INTO qsr_user_balance_log(user_id, type, " +
            "currency_type_id, income, block, balance, description, cause_type_id, cause_id) " +
            "SELECT r.user_id, 1, 3, r.fee, IFNULL(b.block, 0), IFNULL(b.total, 0), i.description, i.causeTypeId, i.cause_id " +
            "FROM (SELECT ? AS outTradeno, ? AS causeTypeId, ? AS description, ? as cause_id) i " +
            "INNER JOIN qsr_payorder_request r ON r.order_number = i.outTradeno AND r.enabled = 1 " +
            "AND r.currency_used = 1 and r.status_id = 4 " +
            "INNER JOIN qsr_users u ON u.id = r.user_id " +
            "LEFT JOIN qsr_user_balance b ON b.user_id = r.user_id";
    private static final String SELECT_PAYORDER_INFO = "SELECT r.order_number, l.level_name purchase_Name " +
            "FROM qsr_payorder_request r " +
            "INNER JOIN qsr_payorder_request_level l ON r.level_id = l.level_id" +
            "WHERE r.user_id = ? AND r.order_number = ? ";
    private static final String CANCEL_PAY_ORDER = "UPDATE qsr_payorder_request r " +
            "SET r.status_id = 6 WHERE r.user_id = ? AND r.order_number = ? AND r.enabled = 1 " +
            "AND r.currency_used = 0 AND r.status_id = 2";
    private static final String DEL_PAY_ORDER = "UPDATE qsr_payorder_request r " +
            "SET r.enabled = 0 WHERE r.user_id = ? AND r.order_number = ?";

    public List<Map<String,Object>> getPayOrderLevel() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_LEVELS));
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

    public PaymentOrder payOrderRequst(int userId, int fee, String provider, String clientIP, Map<String, Object> req) throws ServiceException {
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
                    order[0] = payment.request(p.s("provider_config_id"), fee, clientIP, req, NOTIFY_URL);
                    return null != order[0] && (Db.update(PAY_ORDER_INSTANCE, order[0].getOrderNumber(),
                            req.get("level_en"), req.get("provider"),
                            null == req.get("currency_type_id") ? 1 : req.get("currency_type_id"), userId,
                            req.get("fee"), req.get("currency_amount"), clientIP, req.get("sign_type"),
                            req.toString(), req.toString(), Md5Util.concat(order[0].getConf(), NULL_STRING),
                            order[0].getConf().get("prepay_id"), req.get("platform")) > 0);
                } catch (PaymentException e) {
                    logger.error("payOrderRequest was error. exception = {} ", e);
                    return false;
                }
            });
            if (provider_config_id)
                return order[0];
            else
                throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "订单创建失败");
        } catch (Throwable t) {
            logger.error("payOrderRequest was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "创建订单失败", t);
        }
    }

    public PaymentOrder rePayOrderRequest(int userId, String orderNumber, String provider, String clientIP, Map<String, Object> req) throws ServiceException {
        try {
            Map<String, Object> providerInfo = record2map(Db.findFirst(SELECT_PROVIDER_WITH_PROVIDER_EN, provider));
            if (null == providerInfo) {
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "不存在的支付服务");
            }
            Map<String, Object> orderInfo = record2map(Db.findFirst(SELECT_PAYORDER_INFO, userId, orderNumber));
            if (null == orderInfo) {
                throw new ServiceException(getServiceName(), ErrorCode.NOT_FOUND_SPEC_DATA, "未查询到待支付订单");
            }
            req.put("purchase_Name", orderInfo.get("purchase_Name"));
            req.put("order_number", orderInfo.get("order_number"));
            Parameter p = new Parameter(providerInfo);
            Payment payment = PaymentHelper.getPayment(p.i("provider_config_id"));
            final PaymentOrder[] order = {null};
            boolean provider_config_id = Db.tx(() -> {
                try {
                    order[0] = payment.reRequest(p.s("provider_config_id"), (int) orderInfo.get("fee"),
                            clientIP, req, (String) orderInfo.get("notify_url"));
                    return null != order[0] && Db.update(PAY_ORDER_INSTANCE, orderNumber, orderInfo.get("user_id"),
                            orderInfo.get("fee"), orderInfo.get("currency_amount"), clientIP,
                            orderInfo.get("sign_type"), orderInfo.toString(), orderInfo.toString(),
                            Md5Util.concat(order[0].getConf(), NULL_STRING), order[0].getConf().get("prepay_id"), req.get("platform")) > 0;
                } catch (Throwable t) {
                    logger.error("rePayOrderRequest was error. exception = {}", t);
                    return false;
                }
            });
            if (provider_config_id)
                return order[0];
            else
                throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "订单创建失败");
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "订单创建失败", t);
        }
    }

    public Map<String,Object> getLevelInfo(String typeId) throws ServiceException {
        try {
            return record2map(Db.findById("qsr_payorder_request_level", "level_id", Integer.parseInt(typeId)));
        } catch (Throwable t) {
            logger.error("getLevelInfo was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载档位失败", t);
        }
    }

    public Map<String, Object> getProviderInfo(String provider) throws ServiceException {
        try {
            return record2map(Db.findFirst(PROVIDER_INFO, provider));
        } catch (Throwable t) {
            logger.error("getProviderInfo was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "获取支付提供商失败", t);
        }
    }

    public PageList<Map<String, Object>> getSelfPayorders(int userId, int pageNumber, int pageSize, int statusId)
            throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_PAYORDERS,
                    -1 == statusId ? FROM_PAYORDERS + PAYORDERS_ORDER_BY :
                            FROM_PAYORDERS + TYPES[statusId] + PAYORDERS_ORDER_BY, userId));
        } catch (Throwable t) {
            logger.error("getSelfPayOrders was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "明细加载失败", t);
        }
    }

    public PageList<Map<String, Object>> getPayOrderList(int pageNumber, int pageSize, int userId, String type) throws ServiceException {
        try {
            PageList<Map<String, Object>> pl = null;
            if ("A".equals(type.toUpperCase()))
                pl = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_PAYORDER_WITH_STATUS,
                        FROM_PAYORDER_WITH_ALL_STATUS, userId));
            else if ("O".equals(type.toUpperCase()))
                pl = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_PAYORDER_WITH_STATUS,
                        FROM_PAYORDER_WITH_OVER_STATUS, userId));
            else if ("W".equals(type.toUpperCase()))
                pl = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_PAYORDER_WITH_STATUS,
                        FROM_PAYORDER_WITH_WAIT_STATUS, userId));
            if (null == pl) {
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载订单失败");
            }
            return pl;
        } catch (Throwable t) {
            logger.error("getPayOrderList was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "订单加载失败", t);
        }
    }

    public void saveNotifyRemote(String orderNumber, String provider, String returnCode, String returnMsg, String detail, String res) throws ServiceException {
        try {
            Db.update(SAVE_PAYORDER_NOTIFY, orderNumber, provider, returnCode, returnMsg, detail, res);
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "保存通知失败", t);
        }
    }

    public boolean modifyPayOrderWithNotify(String outTradeNo, int provider, String resultCode, int statusId, String openid) throws ServiceException {
        try {
            boolean flag = Db.tx(() -> {
                try {
                    int[] logIds = {0};
                    return Db.update(MODITY_PAYORDER, statusId, openid, outTradeNo, provider, resultCode) > 0
                            && DbUtil.update(BALANCE_EDCATION, logIds, outTradeNo) > 0
                            && Db.update(BALANCE_EDCATION_LOG, outTradeNo, 1, "购买匠币", logIds[0]) > 0 ;
                } catch (Throwable t) {
                    logger.error("modify notify was error. exception = {} ", t);
                    return false;
                }
            });
            logger.debug("notify result = {}", flag);
            return flag;
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "修改订单失败", t);
        }
    }

    public boolean isDisposed(String outTradeNo) throws ServiceException {
        try {
            return null == Db.findFirst(SELECT_PAYORDER_REQUEST, outTradeNo) ? false : true;
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载订单失败", t);
        }
    }

    public void cancelPayOrder(int userId, String requestId) throws ServiceException {
        try {
            Db.update(CANCEL_PAY_ORDER, userId, requestId);
        } catch (Throwable t) {
            logger.error("cancelPayOrder was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "取消订单失败", t);
        }
    }

    public void delPayOrder(int userId, String requestId) throws ServiceException {
        try {
            Db.update(DEL_PAY_ORDER, userId, requestId);
        } catch (Throwable t) {
            logger.error("delPayOrder was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "删除订单失败", t);
        }
    }
}
