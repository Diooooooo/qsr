package com.qsr.sdk.component.payment;

public class PaymentResponse {

	final String orderNumber;
	final String paymentOrderNumber;
	final PaymentStatus paymentStatus;
	final int paymentFee;
	final String returnCode;
	final String returnMessage;

	public PaymentResponse(String orderNumber, String paymentOrderNumber,
			PaymentStatus paymentStatus, int paymentFee, String returnCode,
			String returnMessage) {
		super();
		this.orderNumber = orderNumber;
		this.paymentOrderNumber = paymentOrderNumber;
		this.paymentStatus = paymentStatus;
		this.paymentFee = paymentFee;
		this.returnCode = returnCode;
		this.returnMessage = returnMessage;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public String getPaymentOrderNumber() {
		return paymentOrderNumber;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public int getPaymentFee() {
		return paymentFee;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

}
