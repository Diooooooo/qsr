package com.qsr.sdk.component.payment.provider.unionpay;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentProvider;

import java.util.Map;

public class UnionPaymentProvider extends AbstractProvider<Payment> implements
        PaymentProvider {

	public static final int PROVIDER_ID = 1;

	public UnionPaymentProvider(int providerId) {
		super(PROVIDER_ID);
	}

	@Override
	public Payment createComponent(int configId, Map<?, ?> config) {
		return new UnionPayment(this);
	}

}