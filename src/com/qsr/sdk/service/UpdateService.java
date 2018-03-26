package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UpdateService extends Service {

    private final static Logger logger = LoggerFactory.getLogger(UpdateService.class);
    private final static String UPDATE = "SELECT u.update_code AS version_code, u.update_name AS version_name, " +
            "IFNULL(u.download_url, '') AS download_url, u.is_force AS is_force FROM qsr_app_update u " +
            "INNER JOIN qsr_app_type t ON u.type_id = t.type_id " +
            "WHERE t.type_id = ? AND u.enabled = 1 ORDER BY u.createtime DESC LIMIT 1 ";
    private static final String HISTORY = "SELECT u.update_id, u.update_name, u.update_content, u.update_images, " +
            "u.update_code, u.update_version, u.is_force, u.download_url, u.enabled, t.type_name " +
            "FROM qsr_app_update u INNER JOIN qsr_app_type t ON u.type_id = t.type_id ORDER BY u.updatetime DESC";

    @CacheAdd(name = "update_check", timeout = 4 , timeUnit = TimeUnit.HOURS)
    public Map<String, Object> update(int mobileType) throws ServiceException {
        try {
            return record2map(Db.findFirst(UPDATE, mobileType));
        } catch (Throwable t) {
            logger.error("update was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "查询更新失败", t);
        }
    }

    @CacheAdd(name = "history", timeout = 4, timeUnit = TimeUnit.HOURS)
    public List<Map<String, Object>> getAllUpdateHistory() throws ServiceException {
        try {
            return record2list(Db.find(HISTORY));
        } catch (Throwable t) {
            logger.error("getAllUpdateHistory was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载历史失败", t);
        }
    }
}
