package com.qsr.sdk.component.thirdaccount.provider.weixin;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.thirdaccount.Account;
import com.qsr.sdk.component.thirdaccount.AccountProvider;

import java.util.Map;

public class WeiXinAccountProvider extends AbstractProvider<Account> implements
        AccountProvider {

	public static final int PROVIDER_ID = 1;

	public WeiXinAccountProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public Account createComponent(int configId, Map<?, ?> config) {
		return new WeiXinAccount(this, configId);
	}

}
