package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.ruleexecutor.Logger;
import com.qsr.sdk.service.ruleexecutor.LoggerFactory;
import com.qsr.sdk.util.ErrorCode;

import java.util.List;
import java.util.Map;

public class BalanceService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);
    private static final String SELECT_BALANCE = "SELECT IFNULL(b.balance, 0) total, t.name currency_name " +
            "FROM qsr_user_currency_type t " +
            "LEFT JOIN qsr_user_balance b ON b.currency_type_id = t.currency_type_id AND b.user_id = ? " +
            "WHERE t.enabled = 1 ";
    private static final String PAY_ESOTERICA = "INSERT into qsr_pay_esoterica(user_id, order_number, esoterica_no, status_id) " +
            "  SELECT i.user_id, MD5(NOW()), e.esoterica_no, 2 FROM (SELECT ? AS user_id, ? AS esoterica_id) i " +
            "  INNER JOIN qsr_team_season_esoterica e ON i.esoterica_id = e.esoterica_no " +
            "  INNER JOIN qsr_users u ON u.id = i.user_id";
    private static final String BALANCE_LOG = "INSERT INTO qsr_user_balance_log(user_id, type, currency_type_id, " +
            "income, block, balance, description, cause_id, cause_type_id) " +
            "  SELECT u.id, 1, 3, 0 - e.esoterica_price, b.block, b.balance - e.esoterica_price, i.description, i.causeId, i.causeTypeId " +
            "FROM (SELECT ? AS userId, ? AS esotericaId, ? AS causeTypeId, ? AS causeId, ? AS description) i " +
            "  INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = i.esotericaId " +
            "  INNER JOIN qsr_users u ON u.id = i.userId " +
            "  LEFT JOIN qsr_user_balance b ON b.user_id = i.userId";
    private static final String CHECK_BALANCE = "SELECT i.userId, i.esotericaId FROM (SELECT ? AS userId, ? AS esotericaId) i " +
            "  INNER JOIN qsr_user_balance b ON i.userId = b.user_id " +
            "  INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = i.esotericaId " +
            "  WHERE b.balance >= e.esoterica_price";

    private static final String CHECK_BALANCE_ESOTERICA = "SELECT i.userId, i.orderNumber FROM (SELECT ? AS userId, ? AS orderNumber) i " +
            "  INNER JOIN qsr_pay_esoterica pe ON i.userId = pe.user_id AND pe.order_number = i.orderNumber " +
            "  INNER JOIN qsr_user_balance b ON i.userId = b.user_id " +
            "  INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = pe.esoterica_no " +
            "  WHERE b.balance >= e.esoterica_price";

    private static final String BALANCE = "UPDATE qsr_user_balance b " +
            "  INNER JOIN (SELECT ? AS userId, ? AS esotericaId) i ON b.user_id = i.userId " +
            "  INNER JOIN qsr_team_season_esoterica e ON e.esoterica_no = i.esotericaId " +
            "SET b.balance = b.balance - e.esoterica_price ";
    private static final String MODIFY_ESOTERICA = "UPDATE qsr_pay_esoterica e " +
            "INNER JOIN qsr_team_season_esoterica se ON e.esoterica_no = se.esoterica_no " +
            "SET e.status_id = 1 " +
            "WHERE e.esoterica_no = ? AND e.status_id = 2 AND e.enabled = 1 AND e.user_id = ? AND e.pay_id = ? ";

    public List<Map<String, Object>> getUserBalance(int userId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_BALANCE, userId));
        } catch (Throwable t) {
            logger.error("getUserBalance was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载匠币失败", t);
        }
    }

    public boolean payEsoterica(int userId, String esotrica_id, long pay) throws ServiceException {
        try {
            return Db.tx(() -> Db.update(MODIFY_ESOTERICA, esotrica_id, userId, pay) > 0
                    && Db.update(BALANCE_LOG, userId, esotrica_id, 2, pay, "购买锦囊") > 0
                    && Db.update(BALANCE, userId, esotrica_id) > 0);
        } catch (Throwable t) {
            logger.error("payEsoterica was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "购买锦囊失败", t);
        }
    }

    public long orderEsoterica(int userId, String esotrica_id) throws ServiceException {
        try {
            long[] pays = {0};
            if(DbUtil.update(PAY_ESOTERICA, pays, userId, esotrica_id) <= 0) {
                logger.error("创建锦囊失败，没有指定编号的锦囊");
                throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "购买锦囊失败");
            }
            return pays[0];
        } catch (Throwable t) {
            logger.error("orderEsoterica was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "购买锦囊失败", t);
        }
    }

    public boolean check(int userId, String esotrica_id) throws ServiceException {
        try {
            return null == Db.findFirst(CHECK_BALANCE, userId, esotrica_id) ? false : true;
        } catch (Throwable t) {
            logger.error("check was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载余额失败", t);
        }
    }

    public boolean checkWithEsoterica(int userId, String orderNumber) throws ServiceException {
        try {
            return null == Db.findFirst(CHECK_BALANCE_ESOTERICA, userId, orderNumber) ? false : true;
        } catch (Throwable t) {
            logger.error("checkWithEsoterica was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载余额失败", t);
        }
    }

    public boolean payEsotericaWithProvider(int userId, String esoterica_id) throws ServiceException {
        try {
            return true;
        } catch (Throwable t) {
            logger.error("payEsotericaWithProvider was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "购买锦囊失败", t);
        }
    }
}
