package com.qsr.sdk.component.payment.provider.rechargecard;

import com.qsr.sdk.component.payment.PaymentProvider;

/**
 * 中国移动充值卡(神州付)
 * 
 * @author yuan
 *
 */
public class CMCCPayment extends PhoneRechargeCardPayment {

	public static final int PROVIDER_ID = 2;

	public CMCCPayment(PaymentProvider provider) {
		super(provider, "cmcc_phone_recharge_card");
	}
}
