package com.qsr.sdk.component.sms.provider.dxt;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.component.sms.SmsSendProvider;

import java.util.Map;

public class DxtSmsProvider extends AbstractProvider<SmsSend> implements
        SmsSendProvider {
	public static final int PROVIDER_ID = 1;

	public DxtSmsProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public SmsSend createComponent(int configId, Map<?, ?> config) {
		return new DxtSms(this);
	}
}
