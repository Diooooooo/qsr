package com.qsr.sdk.service.exception;

import com.qsr.sdk.exception.ApiException;

public class PaymentServiceException extends ServiceException {

	/**   */
	private static final long serialVersionUID = -4448573182728590180L;
	private final String paymentName;

	public PaymentServiceException(String paymentName, String serviceName,
			int code, String message, Throwable t) {
		super(serviceName, code, message, t);
		this.paymentName = paymentName;
	}

	public PaymentServiceException(String paymentName, String serviceName,
			ApiException exception) {
		this(paymentName, serviceName, exception.getCode(), exception
				.getMessage(), exception.getCause());

	}

	public PaymentServiceException(String paymentName, String serviceName,
			int code, String message) {
		this(paymentName, serviceName, code, message, null);
	}

	public String getPaymentName() {
		return paymentName;
	}

}
