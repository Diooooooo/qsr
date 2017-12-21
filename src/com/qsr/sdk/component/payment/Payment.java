package com.qsr.sdk.component.payment;

import com.qsr.sdk.component.Component;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.exception.PaymentException;

import java.util.Map;

public interface Payment extends Component {

	public static int STATUS_NOT_PAY = 0;
	public static int STATUS_WAIT_PAY = 10;
	public static int STATUS_PAY_FAILED = 15;
	public static int STATUS_PAY_SUCCESS = 20;

	public Provider getProvider();

	public PaymentOrder request(String paymentType, int fee, String clientIp,
			Map<String, ?> req) throws PaymentException;

	public PaymentOrder request(String paymentType, int fee, String clientIp,
								Map<String, ?> req, String notifyUrl) throws PaymentException;

	public PaymentResponse payOrder(Map<String, ?> resp) throws PaymentException;

	public PaymentResponse response(Map<String, ?> resp)
			throws PaymentException;

	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req);

	public NotifyContent getNotifyContent(PaymentResponse paymentResult);

}