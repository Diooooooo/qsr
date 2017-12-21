package com.qsr.sdk.exception;

public class PaymentException extends ApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7457253424461348671L;

	public PaymentException(ApiException e) {
		this(e.getCode(), e.getMessage(), e.getCause());
	}

	public PaymentException(int code, String message) {
		super(code, message);
	}

	public PaymentException(int code, String message, Throwable t) {
		super(code, message, t);
	}

}
