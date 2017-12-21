package com.qsr.sdk.component.payment;

import com.qsr.sdk.component.Provider;

public interface PaymentProvider extends Provider {

	public Payment getComponent(int configId);

}
