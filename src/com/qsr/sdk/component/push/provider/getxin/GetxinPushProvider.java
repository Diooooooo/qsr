package com.qsr.sdk.component.push.provider.getxin;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.component.push.PushProvider;

import java.util.Map;

public class GetxinPushProvider extends AbstractProvider<Push> implements
        PushProvider {

	public static final int PROVIDER_ID = 1;

	public GetxinPushProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public Push createComponent(int configId, Map<?, ?> config) {
		return new GetxinPush(this);
	}

}
