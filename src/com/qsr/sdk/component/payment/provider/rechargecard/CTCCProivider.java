package com.qsr.sdk.component.payment.provider.rechargecard;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentProvider;

import java.util.Map;

public class CTCCProivider extends AbstractProvider<Payment> implements
        PaymentProvider {

	public static final int PROVIDER_ID = 4;

	public CTCCProivider() {
		super(PROVIDER_ID);
	}

	@Override
	public Payment createComponent(int configId, Map<?, ?> config) {
		return new CTCCPayment(this);
	}

}