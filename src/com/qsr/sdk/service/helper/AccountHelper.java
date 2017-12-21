package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.thirdaccount.Account;
import com.qsr.sdk.component.thirdaccount.provider.weixin.WeiXinAccount;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

public class AccountHelper {
	public static Account getAccount(int configId) throws ApiException {
		Account account = ComponentProviderManager.getService(Account.class,
				WeiXinAccount.PROVIDER_ID, configId);
		if (account == null) {
			throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
					"不存在的第三方帐号服务");
		}
		return account;
	}
}
