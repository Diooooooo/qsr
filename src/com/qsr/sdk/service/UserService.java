package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserService extends Service {

    final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @CacheAdd(capacity = 2000, timeout = 1, timeUnit = TimeUnit.DAYS)
    protected Integer queryUserIdBySessionKey(String sessionKey) {
        String sql = "select u.id from qsr_users u where u.sessionkey=?";
        return Db.queryInt(sql, sessionKey);
    }

    public int getUserIdBySessionKey(String sessionKey) throws ServiceException {
        Integer userId = queryUserIdBySessionKey(sessionKey);
        if (userId == null) {
            throw new ServiceException(getServiceName(), ErrorCode.SESSION_NOT_EXIST, "sessionkey 不存在");
        }
        return userId;
    }

    @CacheAdd(capacity = 2000, timeout = 2, timeUnit = TimeUnit.HOURS)
    public Map<String, Object> getUserInfo(int userId) {
        if (userId == 0) {
            return null;
        }
        String sql = "SELECT u.sessionkey AS sessionkey, u.mobile AS mobile, u.nickname as nickname, TIMESTAMPDIFF(DAY, u.created, NOW()) AS active_day, " +
                "u.head_img_url AS head_img, u.last_login AS last_login FROM qsr_users u WHERE u.id = ?";
        Map<String, Object> info = record2map(Db.findFirst(sql, userId));
        markString(info, "mobile", 2);
        return info;
    }

    public String login(String mobile, String password, String userType) throws ServiceException {
        String sql = "select u.sessionkey sessionkey from qsr_users u inner join qsr_users_type t on u.type_id = t.type_id " +
                "where u.mobile = ? AND u.passwordkey = md5(?)and t.type_name = ? and u.activated = 1 and !(t.enabled = 0 or t.deleted = 1) = 1";
        Record r = Db.findFirst(sql, mobile, password+Env.getDIVISOR()+mobile, userType);
        if (null == r) {
            throw new ServiceException(getServiceName(), ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "用户不存在");
        }
        return r.get("sessionkey");
    }

    public void exception() throws ServiceException {
        throw new ServiceException(getServiceName(), ErrorCode.ILLEGAL_EXCEPTION, "未知异常");
    }

    public void register(String mobile, String nickName, String password, String confirm, String userType, String ip) throws ServiceException {
        if (!password.equals(confirm)) {
            throw new ServiceException(getServiceName(), ErrorCode.CONFIRM, "两次密码输入不一致");
        }
        String sql = "INSERT INTO qsr_users(type_id, mobile, passwordkey, nickname, sessionkey, last_ip) SELECT t.type_id, i.mobile," +
                "MD5(i.password), i.nickname, MD5(i.mobile), i.ip FROM (SELECT ? as mobile, ? as nickname, ? as userType, ? as password, " +
                "? as ip) i LEFT JOIN qsr_users_type t ON i.userType = t.type_name ";
        try {
            Db.update(sql, mobile, nickName, userType, password + Env.getDIVISOR() + mobile, ip);
        } catch (Throwable e) {
            logger.error("register error. exception={}", e.getMessage());
            throw new ServiceException(getServiceName(), ErrorCode.USER_REGISTER_FAILED, "用户创建失败");
        }
    }

    public void modifyLog(int userId, String clientIp) {
        String sql = "UPDATE qsr_users u SET u.last_ip = ? WHERE u.id = ?" ;
        try {
            Db.update(sql, clientIp, userId);
        } catch (Throwable e){
            logger.error("modifyLog was error, exception = {} ", e);
            logger.error("modify user Last client Ip error, exception={}", e.getMessage());
        }
    }

    private void markString(Map<String, Object> map, String field, int mask) {

        Object value = map.get(field);
        if (value instanceof String) {
            map.put(field, StringUtil.markString((String) value, mask));
        }
    }

    public void modifyUserInfo(int userId, String nickName, String content, int fileId, String sex, String birthday)
            throws ServiceException {
        String sql = "UPDATE qsr_users u SET u.head_img_file_id = ?, u.nickname = ?, u.user_sex = ?, u.birthday = ?";
        String where = " WHERE u.id = ?";
        try {
            Db.update(sql + where, fileId, nickName, sex, birthday, userId);
        } catch (Throwable e) {
            logger.error("modifyUserInfo was error, exception = {} ", e);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "修改信息失败");
        }
    }

    public void resetPassword(String mobile, String newPassword, String confirm) throws ServiceException {
        if (!confirm.equals(newPassword)) {
            throw new ServiceException(getServiceName(), ErrorCode.CONFIRM, "两次输入密码不一致");
        }
        String sql = "UPDATE qsr_users u SET u.passwordkey = MD5(?) WHERE u.mobile = ? ";
        try {
            Db.update(sql, newPassword + mobile, mobile);
        } catch (Throwable e) {
            logger.error("resetPassword was error, exception = {}", e);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "重置密码失败");
        }
    }

    public void modifyHead(int userId, int fileId, String headUrl) throws ServiceException {
        try {
            String sql = "UPDATE qsr_users u SET u.head_img_file_id = ?, u.head_img_url = ? WHERE u.id = ?";
            Db.update(sql, fileId, headUrl, userId);
        } catch (Throwable e) {
            logger.error("modifyHead was error, exception = {}", e);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "修改头像失败");
        }
    }
}
