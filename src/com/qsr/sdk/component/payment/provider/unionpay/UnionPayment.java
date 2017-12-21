package com.qsr.sdk.component.payment.provider.unionpay;

import com.qsr.sdk.component.payment.*;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.MapUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 银联支付
 * 
 * @author yuan
 *
 */
public class UnionPayment extends AbstractPayment {

	// private UpmpUnionPay upmpUnionPay;
	public UnionPayment(PaymentProvider provider) {
		super("unionpay_payment_seq", provider);
		// upmpUnionPay=new
	}

	@Override
	public PaymentOrder request(String paymentType, int paymentFee,
                                String clientIp, Map<String, ?> r, String notifyUrl) throws PaymentException {

		PaymentOrder paymentRequest = super.request(paymentType, paymentFee,
				clientIp, r, notifyUrl);
		Map<String, String> req = new HashMap<String, String>();
		req.put("version", UpmpConfig.VERSION);// 版本号
		req.put("charset", UpmpConfig.CHARSET);// 字符编码
		req.put("transType", "01");// 交易类型
		req.put("merId", UpmpConfig.MER_ID);// 商户代码
		// req.put("backEndUrl", UpmpConfig.MER_BACK_END_URL);// 通知URL
		req.put("backEndUrl", getNotifyUrl());// 通知URL
		req.put("frontEndUrl", UpmpConfig.MER_FRONT_END_URL);// 前台通知URL(可选)
		// req.put("orderDescription", "");// 订单描述(可选)
		req.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss")
				.format(paymentRequest.getOrderTime()));// 交易开始日期时间yyyyMMddHHmmss
		// req.put("orderTimeout", "20141201100000");// 订单超时时间(可选)
		req.put("orderNumber", paymentRequest.getOrderNumber());// 订单号
		req.put("orderAmount", "" + paymentFee);// 订单金额
		req.put("orderCurrency", "156");// 交易币种(可选)
		// req.put("reqReserved", param.get("reqReserved"));//
		// 请求方保留域(可选，用于透传商户信息)

		// 保留域填充方法
		// Map<String, String> merReservedMap = new HashMap<String, String>();
		// merReservedMap.put("test", "test");
		// req.put("merReserved", UpmpService.buildReserved(merReservedMap));//
		// 商户保留域(可选)

		// 银联返回内容
		Map<String, String> resp1 = new HashMap<String, String>();
		String resultString = UpmpUnionPay.submitOrder(req, resp1);
		Map<String, String> resp2 = new HashMap<String, String>();
		if (!UpmpUnionPay.verifyResponse(resultString, resp2)) {
			throw new PaymentException(ErrorCode.DATA_VERIFYDATA_ERROR,
					"银联服务器数据签名验证错误:");
		}
		try {
			Map<String, String> para = UpmpCore.parseQString(resultString);
			String respCode = para.get("respCode");
			String respMsg = para.get("respMsg");
			if (!"00".equals(respCode)) {
				throw new PaymentException(ErrorCode.DATA_VERIFYDATA_ERROR,
						"银联服务器错误:" + respMsg);
			}
			String paymentCode = para.get("tn");
			paymentRequest.setPaymentCode(paymentCode);
			// tn=201412221502190009332
			// {respCode=01, transType=01, signMethod=MD5,
			// respMsg=请求报文错误[0000102], charset=UTF-8,
			// signature=8da08f73daf8d297440960f0ea7b6806, version=1.0.0}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 服务器应答签名验证成功
		// logger.info("服务器应答签名验证成功");
		return paymentRequest;

	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return "00".equals(status) ? PaymentStatus.PaySuccess
				: PaymentStatus.PayFailed;
	}

	@Override
	protected PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {

		// Map requestParam
		// Map<String, String> params = new HashMap<String, String>();
		// Map requestParams = request.getParameterMap();
		// logger.info("银联异步返回消息 【1】未处理：" + requestParams.toString());
		// for (Iterator iter = response.keySet().iterator(); iter.hasNext();) {
		// String name = (String) iter.next();
		// String[] values = (String[]) response.get(name);
		// String valueStr = "";
		// for (int i = 0; i < values.length; i++) {
		// valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr
		// + values[i] + ",";
		// }
		// params.put(name, URLDecoder.decode(valueStr, "utf-8"));
		// }
		Parameter p = new Parameter(resp);
		String orderNumber;
		try {
			orderNumber = p.s("orderNumber");
			String paymentOrderNumber = p.s("qn");
			String status = p.s("transStatus");
			int paymentFee = p.i("settleAmount");

			PaymentStatus paymentStatus = getPaymentStatus(status);

			return new PaymentResponse(orderNumber, paymentOrderNumber,
					paymentStatus, paymentFee, status, null);

		} catch (IllegalArgumentException e) {
			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}

	}

	@Override
	protected boolean verifyData(Map<String, ?> resp) throws PaymentException {

		Map<String, String> resp2 = MapUtil.convertMap(resp);
		for (Map.Entry<String, String> entry : resp2.entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				try {
					entry.setValue(URLDecoder.decode(value, "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return UpmpUnionPay.verifySignature(resp2);
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {
		if (paymentResult != null) {
			return new NotifyContent("success");
		} else {
			return new NotifyContent("failed");
		}
	}

}
