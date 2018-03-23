package com.qsr.sdk.service;

import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.PushHelper;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushService extends Service {

	final static Logger logger = LoggerFactory.getLogger(PushService.class);

	PushService() {

	}
	private int pushSingleMessage(int providerId, String pushUserId,
			String message, int type) throws ServiceException {
		try {
			return PushHelper.pushMessage(providerId, pushUserId, message, type, 1);
		} catch (ApiException e) {
			throw new ServiceException(getServiceName(), e);
		}

	}

	public void pushMessage(int userId, int type, String message)
			throws ServiceException {

		try {
			PushHelper.pushMessage(2, null, message +", type=" + type, type, 2);
		} catch (ApiException e) {
		    throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, e.getMessage(), e);
		}
	}

}
