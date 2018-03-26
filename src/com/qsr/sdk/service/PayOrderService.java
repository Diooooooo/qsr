package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentOrder;
import com.qsr.sdk.component.payment.provider.weixin.WeixinPayment;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.PaymentHelper;
import com.qsr.sdk.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
    private static final String PAY_ORDER_INSTANCE = "INSERT INTO qsr_payorder_request(order_number,request_date,user_id," +
            "request_provider_id,payee_account,level_id,fee,currency_amount,currency_type_id " +
            " ,status_id,client_ip,sign_type,original_body,original_detail,original_data, prepay_id, app_type_id) " +
            "SELECT i.orderNumber, NOW(), i.user_id, p.provider_id, null, IFNULL(l.level_id, i.esotericaNo), IFNULL(l.level_count, i.fee), IFNULL(l.level_count, i.currency_amount), " +
            "  t.currency_type_id, 2, i.clientIp, i.sign_type, i.original_body, i.original_detail, i.original_data, i.prepay_id, IF(null = at.type_id, -1, at.type_id) " +
            "  FROM (SELECT ? AS orderNumber, ? AS level_en, ? AS provider_en, ? AS currency_type_id, ? AS user_id, " +
            "? AS fee, ? AS currency_amount, ? AS clientIp, ? AS sign_type, ? AS original_body, " +
            "? AS original_detail, ? AS original_data, ? as prepay_id, ? AS platform, ? AS esotericaNo) i " +
            "INNER JOIN qsr_payorder_provider p ON p.provider_en = i.provider_en " +
            "INNER JOIN qsr_user_currency_type t ON t.currency_type_id = i.currency_type_id " +
            "LEFT JOIN qsr_payorder_request_level l ON l.level_en = i.level_en " +
            "LEFT JOIN qsr_app_type at ON at.type_name = i.platform " +
            "ON DUPLICATE KEY UPDATE request_date = NOW(), request_provider_id = p.provider_id, level_id = IFNULL(l.level_id, i.fee), " +
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
    private static final String SELECT_PAYORDER_WITH_STATUS = "SELECT q.fee/100 level_count, " +
            "CONCAT('购买锦囊[', e.esoterica_title, ']') title, e.esoterica_title, q.order_number, q.fee/100 fee, " +
            "s.status_id, q.createtime, q.updatetime, e.status_id os_id ";
    private static final String FROM_PAYORDER_WITH_ALL_STATUS = " FROM qsr_payorder_request q " +
            "  INNER JOIN qsr_payorder_request_status s ON q.status_id = s.status_id " +
//            "  INNER JOIN qsr_payorder_request_level l ON q.level_id = l.level_id " +
            "  INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = q.level_id " +
            "  WHERE q.user_id = ? AND q.enabled = 1 " +
            "  AND q.status_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9) " +
            "  ORDER BY q.updatetime DESC";
    private static final String FROM_PAYORDER_WITH_OVER_STATUS = " FROM qsr_payorder_request q " +
            "  INNER JOIN qsr_payorder_request_status s ON q.status_id = s.status_id " +
//            "  INNER JOIN qsr_payorder_request_level l ON q.level_id = l.level_id " +
            "  INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = q.level_id " +
            "  WHERE q.user_id = ? AND q.enabled = 1 " +
            "  AND q.status_id IN (4, 8) " +
            "  ORDER BY q.updatetime DESC";
    private static final String FROM_PAYORDER_WITH_WAIT_STATUS = " FROM qsr_payorder_request q " +
            "  INNER JOIN qsr_payorder_request_status s ON q.status_id = s.status_id " +
//            "  INNER JOIN qsr_payorder_request_level l ON q.level_id = l.level_id " +
            "  INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = q.level_id " +
            "  WHERE q.user_id = ? AND q.enabled = 1 " +
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
//            "AND r.currency_used = 1 AND r.status_id = 8 " +
            "INNER JOIN qsr_users u ON u.id = r.user_id " +
            "ON DUPLICATE KEY UPDATE total = total + r.fee, balance = balance + r.fee, block = block + r.fee";
    private static final String BALANCE_EDCATION_LOG = "INSERT INTO qsr_user_balance_log(user_id, type, " +
            "currency_type_id, income, block, balance, description, cause_type_id, cause_id) " +
            "SELECT r.user_id, 1, 3, r.fee, IFNULL(b.block, 0), IFNULL(b.total, 0), i.description, i.causeTypeId, i.cause_id " +
            "FROM (SELECT ? AS outTradeno, ? AS causeTypeId, ? AS description, ? as cause_id) i " +
            "INNER JOIN qsr_payorder_request r ON r.order_number = i.outTradeno AND r.enabled = 1 " +
//            "AND r.currency_used = 1 and r.status_id = 8 " +
            "INNER JOIN qsr_users u ON u.id = r.user_id " +
            "LEFT JOIN qsr_user_balance b ON b.user_id = r.user_id";
    private static final String SELECT_PAYORDER_INFO = "SELECT r.order_number, e.esoterica_title purchase_Name, " +
            "r.currency_amount fee, r.currency_amount, r.sign_type FROM qsr_payorder_request r " +
            "INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = r.level_id " +
            "WHERE r.user_id = ? AND r.order_number = ? AND r.status_id = 2 AND r.enabled = 1";
    private static final String CANCEL_PAY_ORDER = "UPDATE qsr_payorder_request r " +
            "SET r.status_id = 6 WHERE r.user_id = ? AND r.order_number = ? AND r.enabled = 1 " +
            "AND r.currency_used = 0 AND r.status_id = 2";
    private static final String DEL_PAY_ORDER = "UPDATE qsr_payorder_request r " +
            "SET r.enabled = 0 WHERE r.user_id = ? AND r.order_number = ?";
    private static final String ESOTERICA_ORDER = "INSERT INTO qsr_pay_esoterica(order_number,user_id,esoterica_no) VALUES ( ?, ?, ?)";
    private static final String PAY_ORDER_INFO = "SELECT r.user_id, p.provider_config_id, r.order_number, r.fee FROM qsr_payorder_request r " +
            "INNER JOIN qsr_payorder_result pr ON r.order_number = pr.order_number " +
            "INNER JOIN qsr_team_season_esoterica e ON r.level_id = e.esoterica_no " +
            "INNER JOIN qsr_payorder_provider p ON p.provider_id = r.request_provider_id " +
            "WHERE r.enabled = 1 and r.status_id = 4  and e.status_id = 3 and e.enabled = 1 " +
            "AND r.user_id = ? AND r.order_number = ? ";
    private static final String REFUND_PAY_INFO = "INSERT INTO qsr_payorder_refund_request(" +
            "  order_number,request_date,user_id,payorder_no,fee,currency_type_id,original_body,description) " +
            "  SELECT i.outTradeNo, NOW(), i.userId, i.payOrderNo, i.fee, 4, i.body, i.description " +
            "  FROM (SELECT ? AS outTradeNo, ? AS userId, ? AS payOrderNo, ? AS fee, ? AS body, ? AS description) i";
    private static final String MODIFY_REQUEST_ESOTERICA = "UPDATE qsr_payorder_request r " +
            "INNER JOIN qsr_pay_esoterica e ON r.order_number = e.order_number " +
            "SET r.status_id = 7, e.status_id = 5 " +
            "WHERE r.order_number = ? AND r.user_id = ?";
    private static final String SAVE_REFUND_NOTIFY = "INSERT INTO qsr_payorder_refund_result " +
            "(order_number,provider_id,return_code,return_message,original_body,original_notify) " +
            "SELECT i.orderNumber, p.provider_id, i.returnCode, i.returnMsg, i.notify, i.body " +
            "FROM (SELECT ? AS orderNumber, ? AS provider, ? AS returnCode, ? AS returnMsg, ? AS notify, ? as body) i " +
            "INNER JOIN qsr_payorder_provider p ON p.provider_en = i.provider AND p.enabled = 1 ";
    private static final String MODITY_REFUND = "UPDATE qsr_payorder_refund_request r " +
            "  INNER JOIN qsr_payorder_refund_result rr ON r.payorder_no = rr.order_number " +
            "  SET r.status_id = ? WHERE r.payorder_no = ?";
    private static final String REFUND_INFO = "SELECT r.payorder_no " +
            "FROM qsr_payorder_refund_request r WHERE r.payorder_no = ? AND r.enabled = 1 and r.status_id = 1";

    private static final String PAY_REQUEST_INFO = "SELECT r.user_id, r.order_number FROM qsr_payorder_request r WHERE r.order_number = ?";
    private static final String MODIFY_REQUEST_INFO = "UPDATE qsr_pay_esoterica e SET e.status_id = ? WHERE e.user_id = ? AND e.order_number = ?";

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
            Parameter p = getParameter(provider);
            Payment payment = PaymentHelper.getPayment(p.i("provider_config_id"));
            final PaymentOrder[] order = {null};
            boolean provider_config_id = Db.tx(() -> {
                try {
                    order[0] = payment.request(p.s("provider_config_id"), fee, clientIP, req, Env.getPayNotify());
                    return null != order[0] && (Db.update(PAY_ORDER_INSTANCE, order[0].getOrderNumber(),
                            req.get("level_en"), req.get("provider"),
                            null == req.get("currency_type_id") ? 4 : req.get("currency_type_id"), userId,
                            fee, req.get("currency_amount"), clientIP, req.get("sign_type"),
                            req.toString(), req.toString(), Md5Util.concat(order[0].getConf(), NULL_STRING),
                            order[0].getConf().get("prepay_id"), req.get("platform"), req.get("esoterica_no")) > 0
                            && Db.update(ESOTERICA_ORDER, order[0].getOrderNumber(), userId, req.get("esoterica_no")) > 0
                    );
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
                            clientIP, req, Env.getPayNotify());
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

    public boolean modifyPayOrderWithNotify(String outTradeNo, int provider, String resultCode, int statusId, String openid, int userId) throws ServiceException {
        try {
            boolean flag = Db.tx(() -> {
                try {
                    int[] logIds = {0};
                    boolean a = Db.update(MODITY_PAYORDER, statusId, openid, outTradeNo, provider, resultCode) > 0;
                            boolean b = Db.update(MODIFY_REQUEST_INFO, 1, userId, outTradeNo) > 0;
                            boolean c = DbUtil.update(BALANCE_EDCATION, logIds, outTradeNo) > 0;
                            boolean d = Db.update(BALANCE_EDCATION_LOG, outTradeNo, 1, "购买锦囊", logIds[0]) > 0 ;
                            return a && b && c && d;
                } catch (Throwable t) {
                    logger.error("modify notify was error. exception = {} ", t);
                    return false;
                }
            });
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

    public PaymentOrder payRequestV2(int userId, int level_count, String provider, String realRemoteAddr, Map<String, Object> params) throws ServiceException {
        try {
            Parameter p = getParameter(provider);
            Payment payment = PaymentHelper.getPayment(p.i("provider_config_id"));
            final PaymentOrder[] order = {null};
            boolean provider_config_id = Db.tx(() -> {
                try {
                    order[0] = payment.request(p.s("provider_config_id"), level_count, realRemoteAddr, params, Env.getPayNotify());
                    return null != order[0] && (Db.update(PAY_ORDER_INSTANCE, order[0].getOrderNumber(),
                            params.get("level_en"), params.get("provider"),
                            null == params.get("currency_type_id") ? 1 : params.get("currency_type_id"), userId,
                            params.get("fee"), params.get("currency_amount"), realRemoteAddr, params.get("sign_type"),
                            params.toString(), params.toString(), Md5Util.concat(order[0].getConf(), NULL_STRING),
                            order[0].getConf().get("prepay_id"), params.get("platform"), params.get("type_id"), params.get("type_id")) > 0
                            && Db.update(ESOTERICA_ORDER, order[0].getOrderNumber(), userId, params.get("type_id")) > 0);
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
            logger.error("payRequestV2 was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "发起支付失败，请稍后重试", t);
        }
    }

    public boolean refund(int userId, String orderNo, String clientIp) throws ServiceException {
        try {
            Parameter info = new Parameter(record2map(Db.findFirst(PAY_ORDER_INFO, userId, orderNo)));
            Payment payment = PaymentHelper.getPayment(info.i("provider_config_id"));
            Map<String, String> req = new HashMap<>();
            req.put("out_trade_no", orderNo);
            req.put("fee", String.valueOf(info.i("fee")));
            final PaymentOrder[] order = {null};
            boolean refund = Db.tx(() -> {
                try {
                    order[0] = payment.refund(info.s("provider_config_id"), info.i("fee"), clientIp, req);
                    Map<String, String> o = order[0].getConf();
                            boolean a = Db.update(REFUND_PAY_INFO, o.get("out_refund_no"), userId, orderNo,
                            info.i("fee"), o.get("content"), StringUtil.EMPTY_STRING) > 0;
                            boolean b = Db.update(MODIFY_REQUEST_INFO, 5, userId, orderNo) > 0;
                            boolean c = Db.update(MODIFY_REQUEST_ESOTERICA, orderNo, userId) > 0;
                    return null != order[0] && a && b && c;
                } catch (PaymentException e) {
                    logger.error("refund was error. exception = {}", e);
                    return false;
                }
            });
            if (refund) {
                return true;
            } else {
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "发起退款失败，请稍后重试");
            }
        } catch (Throwable t) {
            logger.error("refund was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "查询接口失败", t);
        }
    }

    public boolean isDisposedOrder(int userId, String orderNo) throws ServiceException {
        try {
            return null == record2map(Db.findFirst(PAY_ORDER_INFO, userId, orderNo)) ? false : true;
        } catch (Throwable t) {
            logger.error("checkOrder was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "暂未查询到对应的订单信息", t);
        }
    }

    private Parameter getParameter(String provider) throws ServiceException {
        Map<String, Object> providerInfo = record2map(Db.findFirst(SELECT_PROVIDER_WITH_PROVIDER_EN, provider));
        if (null == providerInfo) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "不存在的支付服务");
        }
        return new Parameter(providerInfo);
    }

    public void refundNotify(String outTradeNo, int provider, String resultCode, String openid, int userId)
            throws ServiceException {
        try {
            Db.tx(() -> {
                int[] logIds = {0};
                boolean a = Db.update(MODITY_REFUND, 2, outTradeNo) > 0;
                        boolean b = Db.update(MODITY_PAYORDER, 8, openid, outTradeNo, provider, resultCode) > 0;
                        boolean c = Db.update(MODIFY_REQUEST_INFO, 6, userId, outTradeNo) > 0;
                        boolean d = DbUtil.update(BALANCE_EDCATION, logIds, outTradeNo) > 0;
                        boolean e = Db.update(BALANCE_EDCATION_LOG, outTradeNo, 1, "锦囊未中，全额退款", logIds[0]) > 0 ;
                        return a && b && c && d && e;
            });
        } catch (Throwable t) {
            logger.error("refundNotify was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "退款失败", t);
        }
    }

    public void saveRefundNotify(String orderNumber, String provider, String returnCode, String returnMsg, String detail, String res) throws ServiceException {
        try {
            Db.update(SAVE_REFUND_NOTIFY, orderNumber, provider, returnCode, returnMsg, detail, res);
        } catch (Throwable t) {
            logger.error("saveRefundNotify was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "保存通知失败", t);
        }
    }

    public boolean isDisposedRefund(String outTradeNo) throws ServiceException {
        try {
            return null == record2map(Db.findFirst(REFUND_INFO, outTradeNo)) ? false : true;
        } catch (Throwable t) {
            logger.error("isDisposedRefund was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "暂未查询到对应的订单信息", t);
        }
    }

    public Map<String, Object> getPayRequestInfo(String outTradeNo) throws ServiceException {
        try {
            return record2map(Db.findFirst(PAY_REQUEST_INFO, outTradeNo));
        } catch (Throwable t) {
            logger.error("getPayRequestInfo was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载数据失败", t);
        }
    }

}
