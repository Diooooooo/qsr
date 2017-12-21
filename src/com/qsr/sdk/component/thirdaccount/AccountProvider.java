package com.qsr.sdk.component.thirdaccount;

import com.qsr.sdk.component.Provider;

public interface AccountProvider extends Provider {

	public Account getComponent(int configId);

}
