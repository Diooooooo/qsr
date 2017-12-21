package com.qsr.sdk.component.payment;

import java.util.Date;
import java.util.Map;

public class PaymentOrder {

	private final String orderNumber;
	private String paymentCode;
	private long paymentSeq;
	private final Date orderTime;
	private String notifyUrl;

	private final int fee;
	private Map<String, String> conf;

	public PaymentOrder(String orderNumber, String paymentCode, int fee,
			long paymentSeq, Date orderTime, String notifyUrl) {
		super();
		this.orderNumber = orderNumber;
		this.paymentCode = paymentCode;
		this.orderTime = orderTime;
		this.paymentSeq = paymentSeq;
		this.fee = fee;
		this.notifyUrl = notifyUrl;
	}

	public PaymentOrder(String orderNumber, String paymentCode, int fee,
						long paymentSeq, Date orderTime) {
		super();
		this.orderNumber = orderNumber;
		this.paymentCode = paymentCode;
		this.orderTime = orderTime;
		this.paymentSeq = paymentSeq;
		this.fee = fee;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public String getPaymentCode() {
		return paymentCode;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}

	public int getFee() {
		return fee;
	}

	public long getPaymentSeq() {
		return paymentSeq;
	}

	public void setPaymentSeq(long paymentSeq) {
		this.paymentSeq = paymentSeq;
	}

	public Map<String, String> getConf() {
		return conf;
	}

	public void setConf(Map<String, String> conf) {
		this.conf = conf;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getNotifyUrl() {

		return notifyUrl;
	}
}
