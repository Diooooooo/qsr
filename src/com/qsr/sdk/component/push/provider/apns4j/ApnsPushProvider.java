package com.qsr.sdk.component.push.provider.apns4j;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.component.push.PushProvider;

import java.util.Map;

public class ApnsPushProvider extends AbstractProvider<Push> implements
        PushProvider {

	public static final int PROVIDER_ID = 2;

	public ApnsPushProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public Push createComponent(int configId, Map<?, ?> config) {
		return new Apns(this);
	}

}
