package com.qsr.sdk.component.payment.provider.alipay;

import com.qsr.sdk.component.payment.NotifyContent;
import com.qsr.sdk.component.payment.PaymentOrder;
import com.qsr.sdk.component.payment.PaymentResponse;
import com.qsr.sdk.component.payment.PaymentStatus;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里支付
 * 
 * @author yuan
 *
 */
public class AliPayment extends AbstractPayment {

	/**
	 * 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。 迪信通 7-29
	 */
	// private static final String RSA_ALIPAY_PUBLIC =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIiso+7Hou9aUf2zV8r4eU//a5XFnF3djcuqyp8BKNS+qIZkicpnVq6b+VKM7atD4rctZ8K05XLVJoWilHAppVzBVafYbRRti3pmugyIEyXIM0u+8ZCU5q/h8JvfjctAniN+XHJKsghpT+MTfZrorakX0V+uUF4RauRHX/rSerKQIDAQAB";
	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	private static final String STATUS_TRADE_FINISHED = "TRADE_SUCCESS";
	private static final String STATUS_TRADE_WAITPAY = "WAIT_BUYER_PAY";
	public static final int PROVIDER_ID = 1;

	public AliPayment(AliPaymentProvider provider) {

		super("alipay_payment_seq", provider);
	}

	// private boolean verifyData(String content, String sign, String publicKey)
	// {
	// try {
	// String orderNumber = stringParam("out_trade_no");
	// KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
	// byte[] encodedKey = Base64.decode(publicKey);
	// PublicKey pubKey = keyFactory
	// .generatePublic(new X509EncodedKeySpec(encodedKey));
	//
	// java.security.Signature signature = java.security.Signature
	// .getInstance(SIGN_ALGORITHMS);
	//
	// signature.initVerify(pubKey);
	// signature.update(content.getBytes("utf-8"));
	// byte[] signbytes = Base64.decode(sign);
	// boolean bverify = signature.verify(signbytes);
	//
	// return bverify;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return false;
	// }

	@Override
	protected boolean verifyData(Map<String, ?> resp) {

		return true;
		//		Map<String, String> res2 = new HashMap<String, String>();
		//		for (Map.Entry<String, ?> entry : resp.entrySet()) {
		//			// if(entry.getValue()
		//			if (entry.getValue() != null) {
		//				res2.put(entry.getKey(), entry.getValue().toString());
		//			}
		//		}
		//		boolean result2 = AlipayNotify.verify(res2);
		//
		//		return result2;

	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {

		if (STATUS_TRADE_FINISHED.equals(status)) {
			return PaymentStatus.PaySuccess;
		} else if (STATUS_TRADE_WAITPAY.equals(status)) {
			return PaymentStatus.WaitPay;
		} else {
			return PaymentStatus.NotPay;
		}

	}

	@Override
	protected PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {

		// String notifyData = response.get("notify_data");

		// trade_status 用于判断交易状态，值有：
		// TRADE_FINISHED：表示交易成功完成
		// WAIT_BUYER_PAY：表示等待付款

		// {buyer_id=2088302094733373, trade_no=2014122311503037,
		// body=556d9089940229a4ff92199dca64b5051口袋迪信通魔石物品介绍，测试数据口袋迪信通1.0http://www.baidu.com,
		// use_coupon=N, notify_time=2014-12-23 12:56:25, subject=魔石,
		// sign_type=RSA, is_total_fee_adjust=Y, notify_type=trade_status_sync,
		// out_trade_no=27829a3a37e3b3f05155bf46d64c82b0,
		// trade_status=WAIT_BUYER_PAY, discount=0.00,
		// sign=OGKIC/PrlVms5HbIgxFc6Ds1EQAghS2G61oxzmoq3kxRpJkJnFKNi6rLeeF1U6K4PIFBzNmOZn1J4A5+SKelX6NOq/GozMPmUMkTwPf1VtChgDs6GZXbpNohU1BNW2xS+EKnsF10wnzsAqib8/AxGOz3PGFgGMlYiSsqug1J0XY=,
		// buyer_email=liu12921@163.com, gmt_create=2014-12-23 12:56:25,
		// price=0.01, total_fee=0.01, quantity=1, seller_id=2088011700037177,
		// notify_id=c94b4d1ec104da6a93148ab4ee9d302f42,
		// seller_email=dcloudstatistic@126.com, payment_type=1}
		Parameter p = new Parameter(resp);

		int paymentFee;
		try {
			String tradeStatus = p.s("trade_status");
			String paymentOrderNumber = p.s("trade_no");
			String orderNumber = p.s("out_trade_no");
			paymentFee = (int) (p.d("total_fee") * 100);

			PaymentStatus paymentStatus = getPaymentStatus(tradeStatus);

			return new PaymentResponse(orderNumber, paymentOrderNumber,
					paymentStatus, paymentFee, tradeStatus, null);

		} catch (IllegalArgumentException e) {
			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}

	}

	public PaymentOrder request(String paymentType, int fee, String clientIp,
                                Map<String, ?> req, String notifyUrl) throws PaymentException {
//		PaymentOrder result = super.request(paymentType, fee, clientIp, req, notifyUrl);
		long paymentSeq = super.createPaymentSeq();
		PaymentOrder result = new PaymentOrder((String) req.get("order_no"), "", fee, paymentSeq, new Date(), notifyUrl);
		result.setPaymentCode(getSign(req));
		return result;
	}

    @Override
    public PaymentOrder reRequest(String paymentType, int fee, String clientIp, Map<String, ?> req, String notifyUrl) throws PaymentException {
        return null;
    }

    @Override
    public PaymentOrder reRequest(String paymentType, int fee, String clientIp, Map<String, ?> req) throws PaymentException {
        return null;
    }

    public String getSign(Map<String, ?> param) throws PaymentException {
		String requestParam = "partner=\"" + param.get("partner") + "\"";//合作者
		requestParam += "&seller_id=\"" + param.get("seller_id") + "\"";//合作者id
		requestParam += "&out_trade_no=\"" + param.get("order_no") + "\"";//外部订单号
		requestParam += "&subject=\"" + param.get("product_name") + "\"";//商品名称
		requestParam += "&body=\"" + param.get("description") + "\"";//商品详情
		requestParam += "&total_fee=\"" + Double.parseDouble(param.get("fee").toString())/100 + "\"";//商品金额,单位为元
		requestParam += "&notify_url=\"" + param.get("notify_url") + "\"";//回调地址
		requestParam += "&service=\"mobile.securitypay.pay\"";//服务接口名称
		requestParam += "&payment_type=\"1\"";//支付类型
		requestParam += "&_input_charset=\"" + param.get("charset") + "\"";//参数编码
		requestParam += "&it_b_pay=\"" + param.get("out_time") + "\"";//交易订单未付款时自动关闭时间
		requestParam += "&show_url=\"" + param.get("show_url") + "\"";//支付宝处理完请求后自动跳转的页面路径，可为空
		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";
		try {
			String sign = URLEncoder.encode(RSA.sign(requestParam, param.get("private_key").toString(), param.get("charset").toString()), "UTF-8");
			requestParam += "&sign=\"" + sign + "\"";//加密
			requestParam += "&sign_type=\"" + param.get("sign_type").toString() + "\"";//加密类型
		} catch (Exception e) {
			throw new PaymentException(ErrorCode.ILLEGAL_DATA, "获取私钥失败", e);
		}
		return requestParam;
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {
		Map<String, Object> config = new HashMap<String, Object>();
		config.put("hddz", getNotifyUrl());
		config.put("shbh", PartnerConfig.PARTNER);
		config.put("sy", PartnerConfig.PARTNER_PKCS8_PRIVATE_KEY);
		config.put("zh", PartnerConfig.SELLER);
		return config;
	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {
		if (paymentResult != null) {
			return new NotifyContent("success");
		} else {
			return new NotifyContent("failed");
		}
	}

	public static void main(String[] args) throws Exception {

		// // 组装一个待验证签名的Map内容集合
		// Map<String, String> params = new HashMap<String, String>();
		// // 请求的业务内容
		// params.put(
		// "biz_context",
		// "<XML><AppId><![CDATA[2013082200024893]]></AppId><FromUserId><![CDATA[2088102122485786]]></FromUserId><CreateTime>1377228401913</CreateTime>"
		// +
		// "<MsgType><![CDATA[click]]></MsgType><EventType><![CDATA[event]]></EventType><ActionParam><![CDATA[authentication]]></ActionParam><AgreementId><![CDATA[201308220000000994]]></AgreementId><AccountNo><![CDATA[null]]></AccountNo>"
		// +
		// "<UserInfo><![CDATA[{\"logon_id\":\"15858179811\",\"user_name\":\"xxx\"}]]></UserInfo></XML>");
		// // 以下几个参数固定写法
		//
		// params.put("charset", "GBK");
		// params.put("service", "alipay.mobile.public.message.notify");
		// params.put("sign_type", "RSA");
		// params.put(
		// "sign",
		// "rlqgA8O+RzHBVYLyHmrbODVSANWPXf3pSrr82OCO/bm3upZiXSYrX5fZr6UBmG6BZRAydEyTIguEW6VRuAKjnaO/sOiR9BsSrOdXbD5Rhos/Xt7/mGUWbTOt/F+3W0/XLuDNmuYg1yIC/6hzkg44kgtdSTsQbOC9gWM7ayB4J4c=");
		// boolean checkSign = AlipaySignature
		// .rsaCheckV2(
		// params,
		// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB",
		// AlipayConstants.CHARSET_GBK);
		// System.out.println(checkSign);
		String RSA_PRIVATE2 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKzHcAqL0+tT9QWIdQPyMphjeIdK4ctUnZ7kYxALUE0fvAl6NYgKFSXfeWkGN4UZeAaOGzbCY0OudKTtrveCRGKrvzQzky4+r09NVQwGKWVtoZC9fsjZaUm3fgm64+T0erAHnYhOgKvK9RZ81PKn24fuSReL1NccjLM9uEWEIzLpAgMBAAECgYEAku6kDIPu+2CRrVvnTyzH9CobVMrMjDLwPDCzQfCtIHlNWq3wGjmg1G1gfX0I+Aq5tLFi2UWkTulsTtnGgrvoVK8no/8ebIL+i+FZ4cXV8xbzuIy7ukG3SDuV3HVqGUN6vBs0ysQjf9L2dTqOQTwuNTfelu875yTfMfzN4bG8amUCQQDYi+OAowUTghjOvyKygXPGRmvh9qXUlW0j/GsrSpW+ZwVutawx6uNMyHGZQ6qyjKw4P+5gUOpIVX7bl60kynJPAkEAzEIpASkcUInaCSwI3anzmznV34gDSBEdKLTW4hCfY8Vxo+lqxYbJP6c+ZwtzaLyCJAwBRxlZVWfzHeAquOXRRwJAeYPGXmEccB4JHbtUFSdfeFv8HgjydaCEZjU3TkvES9wzyDRaNIjClEvGs2KtXxRhcA8wDQxa68xOK5uppYOVMQJAep0Ymk7AZYRq82iQpee4iaztOzMdrSxA0cfE2o3Z8H1820VqR8rCkkhmCFtyWyQWB2eBeNm5q7Ar2/pOCxJCTwJABfrUmDLwA8m7c53m1r34Hy1g5A2xBZrZhM4ZrYpLjjWNIFAKxR/JIFdXLDXRaPYGR/DKXCcXszVd0zY1R+T40Q==";
		byte[] b = Base64.decode(RSA_PRIVATE2);
		String s = new String(b);
		System.out.println(s);
	}

}
