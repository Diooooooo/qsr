package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushHelper {
	private static final Logger logger = LoggerFactory.getLogger(PushHelper.class);
	public static int pushMessage(int providerId, String pushUserId, String message, int type, int configId) throws ApiException {
		Push push = ComponentProviderManager.getService(Push.class, providerId, configId);
		if (push == null) {
			throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
					"没有找到对应的推送服务");
		}

		try {
			return push.pushSingleMessage(pushUserId, message, type);
		} catch (Exception e) {
		    logger.error("PushHelper was error. class={}, exception={}", push.getClass(), e);
			throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "第三方服务提供异常", e);

		}
	}
}
