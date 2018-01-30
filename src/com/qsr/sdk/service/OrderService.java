package com.qsr.sdk.service;

import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public void transferIn() throws ServiceException {
        try {

        } catch (Throwable t) {
            logger.error("transferIn was error. exception = {}" , t);
            throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "转账失败", t);
        }
    }
}
