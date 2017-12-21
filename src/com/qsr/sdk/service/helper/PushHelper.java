package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

public class PushHelper {
	public static int pushMessage(int providerId, String pushUserId,
			String message, int type) throws ApiException {
		Push push = ComponentProviderManager.getService(Push.class, providerId);
		if (push == null) {
			throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
					"没有找到对应的推送服务");
		}

		try {
			return push.pushSingleMessage(pushUserId, message, type);
		} catch (Exception e) {
			throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"第三方服务提供异常", e);

		}
	}
}
