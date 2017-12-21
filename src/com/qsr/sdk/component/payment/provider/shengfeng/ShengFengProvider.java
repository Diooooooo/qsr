package com.qsr.sdk.component.payment.provider.shengfeng;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentProvider;

import java.util.Map;

public class ShengFengProvider extends AbstractProvider<Payment> implements
        PaymentProvider {

	public static final int PROVIDER_ID = 6;

	public ShengFengProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public Payment createComponent(int configId, Map<?, ?> config) {
		return new ShengFengSMSPayment5(this);
	}

}