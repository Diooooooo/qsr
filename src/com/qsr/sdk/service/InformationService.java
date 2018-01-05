package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InformationService extends Service {
    private final static Logger logger = LoggerFactory.getLogger(InformationService.class);
    private final static String INFORMATION = "INSERT INTO qsr_user_information(user_id ,info_detail) VALUES(?, ?)";

    public void information(int userId, String information) throws ServiceException {
        try {
            Db.update(INFORMATION, userId, information);
        } catch (Throwable t) {
            logger.error("information was error. userId = {}, information = {}, exception = {}", userId, information, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载失败", t);
        }
    }

    public void information(String information) throws ServiceException {
        information(0, information);
    }
}
