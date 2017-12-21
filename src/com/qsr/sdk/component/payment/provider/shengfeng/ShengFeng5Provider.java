package com.qsr.sdk.component.payment.provider.shengfeng;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentProvider;

import java.util.Map;

public class ShengFeng5Provider extends AbstractProvider<Payment> implements
        PaymentProvider {

	public static final int PROVIDER_ID = 9;

	public ShengFeng5Provider() {
		super(PROVIDER_ID);
	}

	@Override
	public Payment createComponent(int configId, Map<?, ?> config) {
		return new ShengFengSMSPayment5(this);
	}

}