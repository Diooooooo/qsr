package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UpdateService extends Service {

    private final static Logger logger = LoggerFactory.getLogger(UpdateService.class);
    private final static String UPDATE = "";

    public Map<String, Object> update(int versionCode) throws ServiceException {
        try {
            return record2map(Db.findFirst(UPDATE, versionCode));
        } catch (Throwable t) {
            logger.error("update was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "查询更新失败", t);
        }
    }
}
