package com.qsr.sdk.component.payment.provider.weixin;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentProvider;

import java.util.Map;

public class WeixinProvider extends AbstractProvider<Payment> implements
        PaymentProvider {

	public static final int PROVIDER_ID = 11;

	public WeixinProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public Payment createComponent(int configId, Map<?, ?> config) {
		return new WeixinPayment(this);
	}

}