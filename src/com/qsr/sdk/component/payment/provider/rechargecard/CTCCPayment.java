package com.qsr.sdk.component.payment.provider.rechargecard;

import com.qsr.sdk.component.payment.PaymentProvider;

/**
 * 电信充值支付(神州付)
 * 
 * @author yuan
 *
 */
public class CTCCPayment extends PhoneRechargeCardPayment {

	public static final int PROVIDER_ID = 4;

	public CTCCPayment(PaymentProvider provider) {
		super(provider, "ctcc_phone_recharge_card");
	}
}
