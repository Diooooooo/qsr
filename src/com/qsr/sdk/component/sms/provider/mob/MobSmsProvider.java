package com.qsr.sdk.component.sms.provider.mob;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.component.sms.SmsSendProvider;

import java.util.Map;

public class MobSmsProvider extends AbstractProvider<SmsSend> implements
        SmsSendProvider {

	public static final int PROVIDER_ID = 2;

	public MobSmsProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public SmsSend createComponent(int configId, Map<?, ?> config) {
		return new MobSms(this);
	}

}
