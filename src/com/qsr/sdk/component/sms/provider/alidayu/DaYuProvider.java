package com.qsr.sdk.component.sms.provider.alidayu;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.component.sms.SmsSendProvider;

import java.util.Map;

/**
 * Created by yuan on 2016/1/26.
 */
public class DaYuProvider extends AbstractProvider<SmsSend> implements SmsSendProvider {

    public static int PROVIDER_ID=3;

    public DaYuProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public SmsSend createComponent(int configId, Map<?,?> config) {
        return new DaYuSms(this,config);
    }
}
