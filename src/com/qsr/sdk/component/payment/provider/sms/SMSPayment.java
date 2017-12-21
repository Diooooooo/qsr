package com.qsr.sdk.component.payment.provider.sms;

import com.qsr.sdk.component.payment.NotifyContent;
import com.qsr.sdk.component.payment.PaymentProvider;
import com.qsr.sdk.component.payment.PaymentResponse;
import com.qsr.sdk.component.payment.PaymentStatus;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 移动短信支付
 * 
 * @author yuan
 *
 */
public class SMSPayment extends AbstractPayment {

	static class FeeDefine {
		public int fee;
		public final String codePrefix;
		public final String codeSuffix;

		public FeeDefine(int fee, String codePrefix, String codeSuffix) {
			this.fee = fee;
			this.codePrefix = codePrefix;
			this.codeSuffix = codeSuffix;
		}

		public FeeDefine(int fee, String code) {
			this(fee, code.substring(0, 55), code.substring(64));

		}

		public boolean isMatchedFee(String paymentCode) {
			return paymentCode.startsWith(codePrefix)
					&& paymentCode.endsWith(codeSuffix);
		}

	}

	public static final String FEE_POINT_YD = "1,2,5,10,20,30,50,100";
	public static final int PROVIDER_ID = 8;

	private static long max_seq = 1000000000;
	private static final char[] code_mask = { '0', '0', '0', '0', '0', '0',
			'0', '0', '0' };

	protected static Map<Integer, FeeDefine> feeDifines = new HashMap<Integer, FeeDefine>();

	static {
		addFeeDefine(new FeeDefine(100,
				"1274151165111007260000007259900112940000000000000000001000000000000317"));
		addFeeDefine(new FeeDefine(200,
				"1274151165111007260000007259900212940000000000000000001000000000000318"));
		addFeeDefine(new FeeDefine(500,
				"1274151165111007260000007259900312940000000000000000001000000000000319"));
		addFeeDefine(new FeeDefine(1000,
				"1274151165111007260000007259900412940000000000000000001000000000000320"));
		addFeeDefine(new FeeDefine(2000,
				"1274151165111007260000007259900612940000000000000000001000000000000321"));
		addFeeDefine(new FeeDefine(3000,
				"1274151165111007260000007259900512940000000000000000001000000000000322"));
		addFeeDefine(new FeeDefine(5000,
				"1274151165111007260000007259900712940000000000000000001000000000000323"));
		addFeeDefine(new FeeDefine(10000,
				"1274151165111007260000007259900812940000000000000000001000000000000324"));
	}

	protected static void addFeeDefine(FeeDefine feeDefine) {
		feeDifines.put(feeDefine.fee, feeDefine);
	}

	public SMSPayment(PaymentProvider provider) {
		super("sms_payment_seq", provider);
	}

	// public PaymentRequest createPaymentRequest(long paymentSeq, int
	// pamentFee) {
	// return Md5.digest(createPaymentCode(paymentSeq, pamentFee));
	// }

	protected String createPaymentCode(long paymentSeq, int paymentFee)
			throws PaymentException {
		int fee = paymentFee;
		FeeDefine feeDefine = feeDifines.get(fee);
		if (feeDefine == null) {
			throw new PaymentException(ErrorCode.PARAMER_ILLEGAL, "错误的支付金额数");
			// return null;
		}
		if (paymentSeq >= max_seq) {
			paymentSeq = paymentSeq % max_seq;
		}
		// String seq = Long.toString(paymentSeq);
		String seq = String.format("%09d", paymentSeq);

		StringBuffer sb = new StringBuffer();
		sb.append(feeDefine.codePrefix);
		// int l = code_mask.length - seq.length();
		// if (l > 0) {
		// sb.append(code_mask, 0, l);
		// }
		sb.append(seq);

		sb.append(feeDefine.codeSuffix);
		return sb.toString();
	}

	@Override
	public PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {
		Parameter p = new Parameter(resp);
		try {
			String status = p.s("status");
			String ccid = p.s("ccid");
			Collection<FeeDefine> fds = feeDifines.values();
			int fee = 0;
			for (FeeDefine f : fds) {
				if (f.isMatchedFee(ccid)) {
					fee = f.fee;
					break;
				}
			}
			String orderNumber = createOrderNumber(ccid, 0, 0);
			String paymentOrderNumber = p.s("linkid");

			PaymentStatus paymentStatus = getPaymentStatus(status);

			return new PaymentResponse(orderNumber, paymentOrderNumber,
					paymentStatus, fee, status, null);
		} catch (IllegalArgumentException e) {
			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}
	}

	@Override
	protected boolean verifyData(Map<String, ?> resp) throws PaymentException {
		return true;
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {
		Map<String, Object> config = new HashMap<String, Object>();
		config.put("feeList", FEE_POINT_YD);
		return config;
	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {
		if (paymentResult != null) {
			return new NotifyContent("success");
		} else {
			return new NotifyContent("0");
		}
	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return "1".equals(status) ? PaymentStatus.PaySuccess
				: PaymentStatus.PayFailed;

	}

}
