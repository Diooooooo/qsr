package com.qsr.sdk.component.payment.provider;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.payment.*;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.service.helper.NumberPool;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.Md5Util;
import com.qsr.sdk.util.UrlUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 支付
 * 
 * @author yuan
 *
 */
public abstract class AbstractPayment extends AbstractComponent implements
        Payment {

	protected static AtomicLong atomLong = new AtomicLong(0);
	protected static final String separation = ":";

	protected static final String ORDERNUMBER_SIGNATUREKEY = "_qsr_";
	private final String numberPoolId;

	// protected Map<String, String> response;

	// protected String paymentSubType;
	// protected int paymentFee;
	// protected long paymentSeq;
	// protected String paymentCode;
	// protected String orderNumber;

	protected AbstractPayment(String numberPoolId, PaymentProvider provider) {
		super(provider);
		this.numberPoolId = numberPoolId;
	}

	protected long createPaymentSeq() {
		return NumberPool.nextLong(numberPoolId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #request(java.lang
	 * .String, int, java.lang.String, java.util.Map)
	 */
	@Override
	public PaymentOrder request(String paymentType, int fee, String clientIp,
                                Map<String, ?> req, String notifyUrl) throws PaymentException {
		long paymentSeq = createPaymentSeq();
		String paymentCode = createPaymentCode(paymentSeq, fee);
		String orderNumber = createOrderNumber(paymentCode, paymentSeq, fee);
		return new PaymentOrder(orderNumber, paymentCode, fee, paymentSeq,
				new Date(), notifyUrl);
	}

	public PaymentOrder request(String paymentType, int fee, String clientIp,
								Map<String, ?> req) throws PaymentException {
		long paymentSeq = createPaymentSeq();
		String paymentCode = createPaymentCode(paymentSeq, fee);
		String orderNumber = createOrderNumber(paymentCode, paymentSeq, fee);
		return new PaymentOrder(orderNumber, paymentCode, fee, paymentSeq,
				new Date());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #payOrder(java.
	 * util.Map)
	 */
	@Override
	public PaymentResponse payOrder(Map<String, ?> resp) {
		return null;

	}

	/**
	 * 创建订单号的因子
	 * 
	 * @return
	 */
	protected Map<String, Object> createOrderNumberFactor(String paymentCode,
			long paymentSeq, int paymentFee) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("paymentProvider", provider.getProviderId());
		result.put("paymentSeq", paymentSeq);
		result.put("paymentFee", paymentFee);
		// result.put("paymentCode", paymentCode);
		// result.put("currentTimeMillis", System.currentTimeMillis());
		result.put("signaturekey", ORDERNUMBER_SIGNATUREKEY);
		return result;

	}

	/**
	 * 创建订单号
	 * 
	 * @param paymentCode
	 * @param paymentSeq
	 * @param paymentFee
	 * @return
	 */
	protected String createOrderNumber(String paymentCode, long paymentSeq,
			int paymentFee) {
		Map<String, Object> factor = createOrderNumberFactor(paymentCode,
				paymentSeq, paymentFee);

		String orderNumber = Md5Util.sign(factor, null);

		return orderNumber;
	}

	/**
	 * 创建支付代码(主要用于短信支付)
	 * 
	 * @param paymentSeq
	 * @param paymentFee
	 * @return
	 */
	protected String createPaymentCode(long paymentSeq, int paymentFee)
			throws PaymentException {
		StringBuffer sb = new StringBuffer();
		sb.append(provider.getProviderId()).append(separation)
				.append(paymentSeq).append(separation)
				.append(System.currentTimeMillis()).append(separation)
				.append(atomLong.incrementAndGet()).append(separation)
				.append(paymentFee);
		return sb.toString();
	}

	// protected void setResponseMap(Map<String, String> response) {
	// this.response = response;
	// }

	// protected int integerParam(String name) throws PaymentException {
	// String value = stringParam(name);
	// return Integer.parseInt(value);
	// }
	//
	// protected int integerParam(String name, int defaultValue) {
	// String value = stringParam(name, null);
	// if (value != null) {
	// return Integer.parseInt(value);
	// } else {
	// return defaultValue;
	// }
	//
	// }
	//
	// protected double doubleParam(String name) throws PaymentException {
	// String value = stringParam(name);
	// return Double.parseDouble(value);
	// }
	//
	// protected double doubleParam(String name, double defaultValue)
	// throws PaymentException {
	// String value = stringParam(name);
	// if (value != null) {
	// return Double.parseDouble(value);
	// } else {
	// return defaultValue;
	// }
	// }
	//
	// protected String stringParam(String name) throws PaymentException {
	// if (response == null) {
	// throw new PaymentException(ErrorCode.PARAMER_ILLEGAL, "参数列表不能为空");
	// }
	// String value = response.get(name);
	// if (value == null || value.trim().length() == 0) {
	// throw new PaymentException(ErrorCode.PARAMER_ILLEGAL, "缺少参数 :"
	// + name + " !");
	// }
	// return value;
	// }
	//
	// protected String stringParam(String name, String defaultValue) {
	// if (response == null) {
	// return defaultValue;
	// }
	// String value = response.get(name);
	// if (value == null) {
	// return defaultValue;
	// }
	// return value;
	// }

	protected abstract boolean verifyData(Map<String, ?> resp)
			throws PaymentException;

	protected abstract PaymentStatus getPaymentStatus(Object status);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #response(java.
	 * util.Map)
	 */
	@Override
	public PaymentResponse response(Map<String, ?> resp)
			throws PaymentException {
		// this.response = resp;
		if (!verifyData(resp)) {
			throw new PaymentException(ErrorCode.DATA_VERIFYDATA_ERROR,
					"数据校验错误");
		}
		return fetchResponse(resp);
	}

	// public String getPaymentSubType() {
	// return paymentSubType;
	// }
	//
	// void setPaymentSubType(String paymentSubType) {
	// this.paymentSubType = paymentSubType;
	// }
	//
	// public int getPaymentFee() {
	// return paymentFee;
	// }
	//
	// void setPaymentFee(int paymentFee) {
	// this.paymentFee = paymentFee;
	// }

	protected String getNotifyUrl() {

		return UrlUtil.getUrl(Env.getHostUrl(), "api/payment_notify/callback/"
				+ this.provider.getProviderId());

	}

	protected String getNotifyUrl(PaymentOrder order, int providerId){
		return UrlUtil.getUrl(order.getNotifyUrl(), providerId);
	}

	protected String getNotifyUrl(PaymentOrder order) {
	    return UrlUtil.getUrl(order.getNotifyUrl(), null);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #getPaymentConfig
	 * (java.lang.String, int, java.util.Map)
	 */
	@Override
	public abstract Map<String, Object> getPaymentConfig(String paymentType,
			int fee, Map<String, ?> req);

	protected abstract PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #getNotifyContent
	 */
	@Override
	public abstract NotifyContent getNotifyContent(PaymentResponse paymentResult);

	@Override
	public abstract PaymentOrder reRequest(String paymentType, int fee, String clientIp, Map<String, ?> req);

	@Override
	public abstract PaymentOrder refund(String paymentType, int fee, String clientIp, Map<String, ?> req) throws PaymentException;

	@Override
    public abstract PaymentOrder refundQuery(String out_trade_no) throws PaymentException;
}
