package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.im.Im;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

public class MessageHelper {

    public static void pushMessage(String from, String to, String message, int type, int providerId, int config) throws ApiException {
        Im im = ComponentProviderManager.getService(Im.class, providerId, config);
        if (null == im) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "没有找到对应的IM服务");
        }
        try {
            im.sendSignMessage(to, from, message, type);
        } catch (Throwable t) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, t.getMessage());
        }
    }
}
