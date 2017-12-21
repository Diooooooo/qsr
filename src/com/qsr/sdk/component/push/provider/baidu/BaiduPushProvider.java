package com.qsr.sdk.component.push.provider.baidu;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.component.push.PushProvider;

import java.util.Map;

public class BaiduPushProvider extends AbstractProvider<Push> implements
        PushProvider {

	public static final int PROVIDER_ID = 3;

	public BaiduPushProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public Push createComponent(int configId, Map<?, ?> config) {
		return new BaiduPush(this);
	}

}
