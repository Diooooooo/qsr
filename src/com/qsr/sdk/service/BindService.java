package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.ParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BindService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(BindService.class);
    //1-身份证。2-手机号
    private static final String[] BIND_TYPE = {"1", "2"};
    private static final String SELECT_BIND_INFO = "SELECT * FROM qsr_user_bind b " +
            "WHERE b.type_id = ? AND b.status_id = 2 AND b.code = ? " +
            "AND b.name = ? AND b.enabled = 1 AND b.deleted = 0";
    private static final String SELECT_BIND_VALID = "SELECT s.finished, s.successed FROM qsr_users u " +
                "  INNER JOIN (SELECT ? AS user_id) i " +
                "  INNER JOIN qsr_user_bind b ON b.user_id = u.id " +
                "  INNER JOIN qsr_user_bind_status s ON s.status_id = b.status_id " +
                "  INNER JOIN qsr_user_bind_type t ON t.type_id = b.type_id " +
                "  WHERE i.user_id = u.id AND t.type_id = ? and s.status_id = 2 and b.enabled = 1 and b.deleted = 0 ";
    private static final String BIND = "INSERT qsr_user_bind (type_id, user_id, status_id, code, name, remark, pic_file1_id, pic_file2_id, pic_file3_id, createdate) " +
            "SELECT i.type_id, i.user_id, i.status_id, i.code, i.name, i.remark, i.pic_file1_id, i.pic_file2_id, i.pic_file3_id, i.createdate " +
            "FROM (SELECT ? as user_id, ? as type_id, ? as status_id, ? as code, ? as name, ? as remark, ? as pic_file1_id, ? as pic_file2_id, ? as pic_file3_id, " +
            "now() as createdate) i " +
            "LEFT JOIN qsr_user_bind b on b.user_id = i.user_id and b.type_id = i.type_id " +
            "LEFT JOIN qsr_user_bind_status s on s.status_id = b.status_id " +
            "WHERE b.bind_id is NULL or s.successed = 0 OR b.enabled = 0 OR b.deleted = 1 " +
            "ON DUPLICATE KEY UPDATE qsr_user_bind.code = values(code), qsr_user_bind.name = values(name), qsr_user_bind.remark = values(remark), " +
            "qsr_user_bind.pic_file1_id = values(pic_file1_id), qsr_user_bind.pic_file2_id = values(pic_file2_id), " +
            "qsr_user_bind.pic_file3_id = values(pic_file3_id), qsr_user_bind.status_id = values(status_id), qsr_user_bind.createdate = values(createdate)," +
            "qsr_user_bind.enabled = 1 , qsr_user_bind.deleted = 0 ";

    public boolean isBindIDCard(int userId) throws ServiceException {
        try {
            return checkBindSuccessByTypeId(userId, BIND_TYPE[0]);
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载绑定信息失败", t);
        }
    }

    public void bindIdCard(int userId, String cardName, String cardNumber, int frontFileId, int reverseFileId, int holdFileId) throws ServiceException {
        Record r = Db.findFirst(SELECT_BIND_INFO, BIND_TYPE[0], cardNumber.toLowerCase(), cardName.toLowerCase());
        if (null != r) {
            throw new ServiceException(getServiceName(), ErrorCode.ILLEGAL_DATA, "已使用的身份证，请更换身份证");
        }

        if (checkBindSuccessByTypeId(userId, BIND_TYPE[0])) return;

        int[] rows = {0};
        int re = DbUtil.update(BIND, rows, userId, BIND_TYPE[0], 1, cardNumber.toLowerCase(), cardName.toLowerCase(), null, frontFileId, reverseFileId, holdFileId);

        if (rows[0] > 0) {
            // 插入成功
        } else if (re == 2) {
            //更新成功
        } else {
            logger.error("bindIdCard was error. exception = {}", "绑定身份证失败");
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "绑定身份证失败");
        }
    }

    //TODO
    public void bindMobile(int userId, String mobile) throws ServiceException {

    }

    private boolean checkBindSuccessByTypeId(int userId, String typeId) {
        Record r = Db.findFirst(SELECT_BIND_VALID, userId, typeId);
        Parameter p = record2param(r);
        boolean isBind = false;
        if (null != p) {
            if (ParameterUtil.bool(p.o("successed", null)) && ParameterUtil.bool(p.o("finished", null))) {
                isBind = true;
            }
        }
        return isBind;
    }
}
