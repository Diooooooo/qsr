package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.sms.SendResult;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

import java.util.Map;

/**
 * Created by yuan on 2016/1/26.
 */
public class SmsSendHelper {
    public static int default_provider=3;
    public static int default_service=1;

    public static SendResult sendSms(String phoneNumber, int template, Map<String,String> templateParams) throws ApiException {
        SmsSend smsSend = ComponentProviderManager.getService(SmsSend.class, 3, 1);
        if (smsSend == null) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
                    "不存在的短信服务");
        }
        return smsSend.send(phoneNumber,template+"",templateParams);

    }
}
