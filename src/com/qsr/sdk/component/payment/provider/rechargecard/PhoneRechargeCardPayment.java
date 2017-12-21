package com.qsr.sdk.component.payment.provider.rechargecard;

import com.qsr.sdk.component.payment.NotifyContent;
import com.qsr.sdk.component.payment.PaymentProvider;
import com.qsr.sdk.component.payment.PaymentResponse;
import com.qsr.sdk.component.payment.PaymentStatus;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.Md5Util;

import java.util.Map;

/**
 * 充值卡支付(神州付)
 * 
 * @author yuan
 *
 */
public abstract class PhoneRechargeCardPayment extends AbstractPayment {

	private static String privateKey = "f1M03C693f=";

	protected PhoneRechargeCardPayment(PaymentProvider provider,
			String numberPoolId) {
		super(numberPoolId, provider);
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {

		return null;
	}

	@Override
	protected boolean verifyData(Map<String, ?> resp) throws PaymentException {
		try {
			Parameter p = new Parameter(resp);
			String version = p.s("version", null); // 获取神州付消费接口的版本号
			String merId = p.s("merId"); // 获取商户ID
			String paymentFee = p.s("payMoney"); // 获取消费金额
			String orderNumber = p.s("orderId"); // 获取商户订单号
			String status = p.s("payResult"); // 获取交易结果,1 成功 0 失败

			String privateField = p.s("privateField", null); // 获取商户私有数据
			String payDetails = p.s("payDetails"); // 获取消费详情
			String returnMd5String = p.s("md5String"); // 获取MD5加密串
			String signString = p.s("signString"); // 神州付证书签名
			String cardMoney = p.s("cardMoney"); // 神州付证书签名
			String combineString = version + merId + paymentFee + orderNumber
					+ status + privateField + payDetails + privateKey;
			String md5String = Md5Util.digest(combineString);

			return md5String.equalsIgnoreCase(returnMd5String);
		} catch (IllegalArgumentException e) {
			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}
	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return ("1".equals(status)) ? PaymentStatus.PaySuccess
				: PaymentStatus.PayFailed;
	}

	@Override
	public PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {

		try {
			Parameter p = new Parameter(resp);
			String version = p.s("version", null); // 获取神州付消费接口的版本号
			String merId = p.s("merId"); // 获取商户ID
			int paymentFee = p.i("payMoney"); // 获取消费金额
			String orderNumber = p.s("orderId"); // 获取商户订单号
			String status = p.s("payResult"); // 获取交易结果,1 成功 0 失败

			String privateField = p.s("privateField", null); // 获取商户私有数据
			String payDetails = p.s("payDetails"); // 获取消费详情
			String returnMd5String = p.s("md5String"); // 获取MD5加密串
			String signString = p.s("signString"); // 神州付证书签名
			String cardMoney = p.s("cardMoney"); // 神州付证书签名

			PaymentStatus paymentStatus = getPaymentStatus(status);
			// double doubleFee = Double.parseDouble(paymentFee);
			return new PaymentResponse(orderNumber, orderNumber, paymentStatus,
					paymentFee, status, null);
		} catch (IllegalArgumentException e) {
			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}
	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {

		if (paymentResult != null) {
			return new NotifyContent(paymentResult.getOrderNumber());
		} else {
			return new NotifyContent("failed");
		}

	}

}
