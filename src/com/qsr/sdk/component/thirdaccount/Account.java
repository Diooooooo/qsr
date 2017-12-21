package com.qsr.sdk.component.thirdaccount;

import com.qsr.sdk.component.Component;

public interface Account extends Component {

	public AccountInfo getAccountInfo(String userId, String token)
			throws Exception;

	public int getConfigId();
}
