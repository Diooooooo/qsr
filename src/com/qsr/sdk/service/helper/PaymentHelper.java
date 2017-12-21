package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.payment.Payment;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

public class PaymentHelper {

	public static Payment getPayment(int providerId) throws ApiException {
		Payment payment = ComponentProviderManager.getService(Payment.class,
				providerId);

		if (payment == null) {
			throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
					"不存在的支付服务");
		}
		return payment;
	}
}
