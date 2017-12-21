package com.qsr.sdk.component.payment.provider.rechargecard;

import com.qsr.sdk.component.payment.PaymentProvider;

/**
 * 联通充值卡支付(神州付)
 * 
 * @author yuan
 *
 */
public class CUCCPayment extends PhoneRechargeCardPayment {

	public static final int PROVIDER_ID = 3;

	public CUCCPayment(PaymentProvider provider) {
		super(provider, "cucc_phone_recharge_card");
	}
}
