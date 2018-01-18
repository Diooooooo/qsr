package com.qsr.sdk.service;

import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.MessageHelper;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public void sendMessage(String fromTo, String sendTo, String message, int type) throws ServiceException {
        try {
            MessageHelper.pushMessage(fromTo, sendTo, message, type, 1, 1);
        } catch (Throwable t) {
            logger.error("sendMessage was error, exception = {}, from={}, send={}, message={}, type={}", t, fromTo, sendTo, message, type);
            throw new ServiceException(getServiceName(), ErrorCode.REPEAT_OPERATION, "消息发送失败", t);
        }
    }
}
