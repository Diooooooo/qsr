package com.qsr.sdk.component.payment.provider.shengfeng;

import com.qsr.sdk.component.payment.*;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.Md5Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 北京盛峰远景支付
 * 
 * @author yuan
 *
 */
public class ShengFengSMSPayment extends AbstractPayment {

	/** 短信类子类型 - 移动 */
	protected static final String SUBTYPE_YD = "0";

	/** 短信类子类型- */
	protected static final String SUBTYPE_LD = "1";

	/** 短信类子类型 - */
	protected static final String SUBTYPE_DX = "2";

	protected static String MCHID = "XT";
	protected static String KEY = "23B17A440908473B";

	public static final int PROVIDER_ID = 6;

	private static long max_seq = 1000000000;
	private static final char[] code_mask = { '0', '0', '0', '0', '0', '0',
			'0', '0', '0', '0' };
	/**
	 * 计费点（面额）
	 */
	protected static final Map<String, List<Integer>> FeeList1 = new HashMap<String, List<Integer>>();
	/**
	 * 计费点（面额）
	 */
	protected static final Map<String, String> FeeList2 = new HashMap<String, String>();
	static {

		// Object o=new Integer[]{1,2,3};
		// new ArrayList(new Integer[]{0,0});
		FeeList1.put(SUBTYPE_YD, Arrays.asList(200, 500, 1000, 2000, 3000));
		FeeList1.put(SUBTYPE_LD, Arrays.asList(200, 500, 1000, 2000, 3000));
		FeeList1.put(SUBTYPE_DX, Arrays.asList(200, 500, 1000, 2000, 3000));

		FeeList2.put(SUBTYPE_YD, "2,5,10,20,30");
		FeeList2.put(SUBTYPE_LD, "2,5,10,20,30");
		FeeList2.put(SUBTYPE_DX, "2,5,10,20,30");
	}

	public ShengFengSMSPayment(PaymentProvider provider) {
		super("sf_sms_payment_seq", provider);
	}

	@Override
	protected boolean verifyData(Map<String, ?> resp) throws PaymentException {

		// http://ip:port/XXX.XXX?MchNo=***&Phone=***&Fee=****&OrderId=****&Sign=****
		// Sign = md5(MchNo+Phone+Fee+OrderId+Key)

		StringBuffer sb = new StringBuffer();

		String sign = (String) resp.get("Sign");
		sb.append(resp.get("MchNo")).append(resp.get("Phone"))
				.append(resp.get("Fee")).append(resp.get("OrderId"));
		sb.append(KEY);

		// logger.info("retStr : [" + retStr + "]");
		String md5 = Md5Util.digest(sb.toString());

		return md5.equalsIgnoreCase(sign);

	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {
		Map<String, Object> conf = new HashMap<String, Object>();
		if (paymentType != null) {
			String paymentPointList = FeeList2.get(paymentType);
			if (paymentPointList != null) {
				conf.put("feeList", paymentPointList);
			}
			conf.put("hddz", getNotifyUrl());
			conf.put("shbh", MCHID);
			conf.put("sy", KEY);

		}
		return conf;
	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return PaymentStatus.PaySuccess;
	}

	@Override
	protected PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {
		try {
			Parameter p = new Parameter(resp);
			String paymentCode = p.s("OrderId");
			String orderNumber = createOrderNumber(paymentCode, 0, 0);

			int fee = p.i("Fee");
			PaymentStatus paymentStatus = getPaymentStatus(null);
			return new PaymentResponse(orderNumber, paymentCode, paymentStatus,
					fee, "", null);

		} catch (IllegalArgumentException e) {
			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}
	}

	@Override
	protected Map<String, Object> createOrderNumberFactor(String paymentCode,
			long paymentSeq, int paymentFee) {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("paymentProvider", this.getProvider().getProviderId());

		result.put("paymentCode", paymentCode);

		result.put("MCHID", MCHID);
		result.put("KEY", KEY);

		result.put("signaturekey", ORDERNUMBER_SIGNATUREKEY);

		return result;
	}

	@Override
	public PaymentOrder request(String paymentType, int fee, String clientIp,
                                Map<String, ?> req, String notifyUrl) throws PaymentException {
		if (paymentType == null) {
			throw new PaymentException(ErrorCode.PARAMER_ILLEGAL, "缺少子支付类型参数");
		}

		List<Integer> feeList = FeeList1.get(paymentType);
		if (feeList == null) {
			throw new PaymentException(ErrorCode.PARAMER_ILLEGAL, "不存在的子支付类型");
		}
		if (!feeList.contains(fee)) {
			throw new PaymentException(ErrorCode.PARAMER_ILLEGAL, "错误的支付金额数");
		}

		return super.request(paymentType, fee, clientIp, req, notifyUrl);
	}

	protected String createPaymentCode(long paymentSeq, int paymentFee)
			throws PaymentException {
		// String paymentCode = super.createPaymentCode();
		// paymentCode = Md5.digest(paymentCode).substring(0, 10);
		if (paymentSeq >= max_seq) {
			paymentSeq = paymentSeq % max_seq;
		}
		String seq = String.format("%09d", paymentSeq);
		// String seq = Long.toString(paymentSeq);
		//
		// StringBuffer sb = new StringBuffer();
		// int l = code_mask.length - seq.length();
		// if (l > 0) {
		// sb.append(code_mask, 0, l);
		// }
		// sb.append(seq);
		return MCHID + seq;
	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {

		if (paymentResult != null) {
			return new NotifyContent("000~success~");
		} else {
			return new NotifyContent("555~failed~");
		}

	}

}
