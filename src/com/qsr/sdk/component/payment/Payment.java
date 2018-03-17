package com.qsr.sdk.component.payment;

import com.qsr.sdk.component.Component;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.exception.PaymentException;

import java.util.Map;

public interface Payment extends Component {

	int STATUS_NOT_PAY = 0;
	int STATUS_WAIT_PAY = 10;
	int STATUS_PAY_FAILED = 15;
	int STATUS_PAY_SUCCESS = 20;

	Provider getProvider();

	PaymentOrder request(String paymentType, int fee, String clientIp,
			Map<String, ?> req) throws PaymentException;

	PaymentOrder request(String paymentType, int fee, String clientIp,
								Map<String, ?> req, String notifyUrl) throws PaymentException;

	PaymentOrder reRequest(String paymentType, int fee, String clientIp, Map<String, ?> req,
                                  String notifyUrl) throws PaymentException;

    PaymentOrder reRequest(String paymentType, int fee, String clientIp, Map<String, ?> req)
            throws PaymentException;

    PaymentOrder refund(String paymentType, int fee, String clientIp, Map<String, ?> req) throws PaymentException;

    PaymentOrder refundQuery(String out_trade_no) throws PaymentException;

	PaymentResponse payOrder(Map<String, ?> resp) throws PaymentException;

	PaymentResponse response(Map<String, ?> resp)
			throws PaymentException;

	Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req);

	NotifyContent getNotifyContent(PaymentResponse paymentResult);

}