package com.qsr.sdk.component.payment.provider.pointcard;

import com.qsr.sdk.component.payment.NotifyContent;
import com.qsr.sdk.component.payment.PaymentProvider;
import com.qsr.sdk.component.payment.PaymentResponse;
import com.qsr.sdk.component.payment.PaymentStatus;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.Md5Util;

import java.util.HashMap;
import java.util.Map;

/**
 * 天宏(充值卡/点卡)
 * 
 * @author yuan
 *
 */
public class PointCardPayment extends AbstractPayment {

	public static final int PROVIDER_ID = 7;
	/** 商户编号 必输, 由本公司分配2位以上数字 **/
	private static String corpID = "182";
	/** 商户名称 必输，由本公司提供 **/
	private static String corpName = "迪信通";
	/** 订单号 必输，客户支付后商户网站产生的一个唯一的定单号，该订单号不能重复。 **/
	// String orderID = orderidString;
	// des密码key
	private static String desKeyString = "12df1D@3";
	/** 接收支付结果的通知地址 必输，商户用来接收订单支付结果的URL；我们使用HTTP协议POST方式向此地址发送交易结果 **/
	private static String notifyURL = "http://www.91xiyou.com/dxt_game/zf/zf_receiveOrderInfoForDK.action";
	/** 用户标识 必输，必须唯一确定用户身份。由本公司提供 **/
	private static String userFlag = "dxt";
	/** 用户名 必输，由本公司提供 **/
	private static String userName = "dixintong";
	/** 用户Ip必输 **/
	private static String userIP = "118.145.22.46";
	/** 订单签名数据 必输，数字签名,参见:1.3.2，确保订单安全 **/
	private static String md5key = "12df1D@3";

	// /---------------------
	private static String KEY = "12df1D@3";

	// public static String corpID = "194";
	// public static String corpName = "迪信通";

	public PointCardPayment(PaymentProvider provider) {
		super("pointcard_payment_seq", provider);
	}

	private static Map<Integer, String> returnMsg = new HashMap<Integer, String>();

	static {
		returnMsg.put(301, "您输入的充值卡密码错误");
		returnMsg.put(302, "您输入的充值卡已被使用");
		returnMsg.put(303, "您输入的充值卡密码非法");
		returnMsg.put(304, "您输入的卡号或密码错误次数过多");
		returnMsg.put(305, "卡号密码正则不匹配或者被禁止");
		returnMsg.put(306, "本卡之前被提交过，本次订单失败，不再继续处理");
		returnMsg.put(307, "暂不支持该充值卡的支付");
		returnMsg.put(308, "您输入的充值卡卡号错误");
		returnMsg.put(309, "您输入的充值卡未激活");
		returnMsg.put(310, "您输入的充值卡已经作废");
		returnMsg.put(311, "您输入的充值卡已过期");
		returnMsg.put(312, "您选择的卡面额不正确");
		returnMsg.put(313, "该卡为特殊本地业务卡，系统不支持");
		returnMsg.put(314, "该卡为增值业务卡，系统不支持");
		returnMsg.put(315, "新生卡");
		returnMsg.put(316, "系统维护");
		returnMsg.put(317, "接口维护");
		returnMsg.put(318, "运营商系统维护");
		returnMsg.put(319, "系统忙，请稍后再试");
		returnMsg.put(320, "未知错误");
		returnMsg.put(301, "您输入的充值卡密码错误");
		returnMsg.put(321, "本卡之前被处理完毕，本次订单失败，不再继续处理");
		returnMsg.put(322, "该地区运营商系统维护");

	}

	@Override
	protected boolean verifyData(Map<String, ?> resp) throws PaymentException {

		String sign = (String) resp.get("verifyMD5");
		StringBuffer sb = new StringBuffer();
		sb.append(resp.get("corpID"));
		sb.append(resp.get("orderID"));
		sb.append(resp.get("state"));
		sb.append(resp.get("payMoney"));
		sb.append(KEY);

		return Md5Util.digest(sb.toString()).equalsIgnoreCase(sign);
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {
		// TODO Auto-generated method stub
		Map<String, Object> conf = new HashMap<String, Object>();
		conf.put("corpID", corpID);
		conf.put("corpName", corpName);
		conf.put("desKeyString", desKeyString);
		conf.put("notifyURL", getNotifyUrl());
		conf.put("userFlag", userFlag);
		conf.put("userName", userName);
		conf.put("userIP", userIP);
		conf.put("md5key", md5key);
		return conf;
	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return ("1".equals(status)) ? PaymentStatus.PaySuccess
				: PaymentStatus.PayFailed;
	}

	@Override
	protected PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {
		// TODO Auto-generated method stub
		try {
			Parameter p = new Parameter(resp);
			String orderNumber = p.s("orderID");
			double paymentFee = p.d("payMoney") * 100;
			String status = p.s("state");
			String returnCode;
			PaymentStatus paymentStatus = getPaymentStatus(status);
			String returnMessage = null;
			if (paymentStatus.isSuccess()) {
				returnCode = status;
			} else {
				returnCode = p.s("errcode");
				if (returnCode != null) {
					try {
						returnMessage = returnMsg.get(Integer
								.parseInt(returnCode));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
			return new PaymentResponse(orderNumber, orderNumber, paymentStatus,
					(int) paymentFee, returnCode, returnMessage);
		} catch (IllegalArgumentException e) {
			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}
	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {

		if (paymentResult != null) {
			return new NotifyContent("1");
		} else {
			return new NotifyContent("0");
		}

	}

}
