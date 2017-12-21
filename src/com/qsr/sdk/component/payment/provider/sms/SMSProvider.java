package com.qsr.sdk.component.payment.provider.sms;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.component.payment.PaymentProvider;

import java.util.Map;

public class SMSProvider extends AbstractProvider<Payment> implements
        PaymentProvider {

	public static final int PROVIDER_ID = 8;
	Payment payment;

	public SMSProvider() {
		super(PROVIDER_ID);
		payment = new SMSPayment(this);
	}

	@Override
	public Payment getComponent(int configId) {
		return payment;
	}

	@Override
	public Payment createComponent(int configId, Map<?, ?> config) {
		return new SMSPayment(this);
	}

}