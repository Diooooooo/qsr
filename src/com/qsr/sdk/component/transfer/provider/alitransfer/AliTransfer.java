package com.qsr.sdk.component.transfer.provider.alitransfer;

import com.qsr.sdk.component.transfer.*;
import com.qsr.sdk.component.transfer.provider.AbstractTransfer;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.Md5Util;

import java.util.*;

public class AliTransfer extends AbstractTransfer {
	/**
	 * 支付宝提供给商户的服务接入网关URL(新)
	 */
	private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do";

	private static final String service_name = "batch_trans_notify";
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	private static final String partner = "2088811007489561";
	// 2088811007489561
	// zuciuga74g80f5ib7g3rbtloxlx4nv92
	// 商户的私钥
	private static final String key = "zuciuga74g80f5ib7g3rbtloxlx4nv92";

	private static final String payer_account = "zhaopin@bjtxyj.com";
	private static final String payer_accountname = "北京天行远景科技发展有限公司";
	// 字符编码格式 目前支持 gbk 或 utf-8
	private static String input_charset = "utf-8";

	// 签名方式 不需修改
	private static String sign_type = "MD5";

	private static final String demo = "天行币兑换";

	private static final String SP = "^";
	public static final int TRANSFER_TYPE = 1;
	private static int min_service_fee = 100;
	private static int max_service_fee = 2500;
	private static double service_rate = 0.005;
	// private static double currency_rate = 1.0;
	private static final NotifyContent success = new NotifyContent("success");
	private static final NotifyContent failed = new NotifyContent("failed");

	public AliTransfer(TransferProvider provider) {
		super(provider, "alipay_transfer_seq");
	}

	private static final Map<String, String> reasons = new HashMap<String, String>();

	public static final int PROVIDER_ID = 1;

	static {
		reasons.put("ACCOUN_NAME_NOT_MATCH", "支付宝帐号和名称不匹配");
		reasons.put("ERROR_OTHER_CERTIFY_LEVEL_LIMIT", "支付宝帐号认证等级不够");
		reasons.put("ERROR_OTHER_NOT_REALNAMED", "支付宝帐号没有真实姓名");
		reasons.put("RECEIVE_USER_NOT_EXIST", "支付宝帐号不存在");

		reasons.put("DETAIL_CONSULT_CHECK_ERROR", "DETAIL_CONSULT_CHECK_ERROR");

		reasons.put("ILLEGAL_USER_STATUS", "无效的支付宝帐号状态");

		reasons.put("RECEIVE_ACCOUNT_ERROR", "支付宝帐号错误");
		reasons.put("RECEIVE_EMAIL_ERROR", "支付宝邮件错误");
		reasons.put("TRANSFER_AMOUNT_NOT_ENOUGH", "系统暂停兑换:请重新兑换");
	}

	private static String getReasonByCode(String code) {
		String result = null;
		if (code != null) {
			result = reasons.get(code);
		}
		if (result == null) {
			result = code;
		}
		return result;

	}

	@Override
	public int calcFee(int totalFee) throws ApiException {
		// double totalFee = currencyAmount * currencyRate;

		// totalFee = totalFee * (1 - companyServiceFeeRate);

		double serviceFee = totalFee / (1 + service_rate) * service_rate;

		if (serviceFee < min_service_fee) {
			serviceFee = min_service_fee;
		} else if (serviceFee > max_service_fee) {
			serviceFee = max_service_fee;
		}

		double fee = totalFee - serviceFee;
		if (fee < 1) {
			throw new ApiException(ErrorCode.ILLEGAL_DATA, "不够支付服务费");
		}
		return (int) Math.floor(fee);
	}

	@Override
	public TransferRequest transferRequest(String payeeAccount,
                                           String payeeAccountName, int actualFee) throws ApiException {

		// double fee = calcFee(currencyType, currencyAmount, currencyRate,
		// companyServiceFeeRate);
		// if (fee < 1) {
		// throw new ApiException(ErrorCode.ILLEGAL_DATA, "不够支付服务费");
		// }
		// // 单位：分
		// int ifee = (int) Math.floor(fee * 100);

		TransferRequest transferRequest = new TransferRequest(actualFee,
				payeeAccount, payeeAccountName);

		return transferRequest;
	}

	protected String buildSign(Map<String, ?> request, String signType) {
		List<String> keys = new ArrayList<String>(request.keySet());
		Collections.sort(keys);

		StringBuffer sb = new StringBuffer();

		for (String key : keys) {
			Object value = request.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append("&");
			}

			sb.append(key);
			sb.append("=");
			sb.append(value);

		}

		sb.append(key);

		String sign = Md5Util.digest(sb.toString(), input_charset);

		return sign;
	}

	private TransferResponseItem createItem(String s) {

		TransferResponseItem item = null;
		String[] strItem = s.split("\\^");
		if (strItem.length > 7) {
			try {
				String reason = getReasonByCode(strItem[5]);
				item = new TransferResponseItem("S".equals(strItem[4]),
						Integer.parseInt(strItem[0]), strItem[1], strItem[2],
						(int) (Double.parseDouble(strItem[3]) * 100), reason,
						strItem[6], strItem[7]);
			} catch (Throwable e) {

				e.printStackTrace();
			}
		}

		return item;

	}

	@Override
	public TransferResponse handleNotify(Map<String, String> notifyParams)
			throws ApiException {

		String sign1 = buildSign(notifyParams, sign_type);

		Parameter p = new Parameter(notifyParams);

		String sign2 = p.s("sign");
		if (!sign1.equalsIgnoreCase(sign2)) {
			throw new ApiException(ErrorCode.DATA_VERIFYDATA_ERROR, "数据校验错误");
		}

		String notifyTime = p.s("notify_time");
		// batch_trans_notify
		String notifyType = p.s("notify_type");
		String notifyId = p.s("notify_id");
		String signType = p.s("sign_type");

		String orderNumber = p.s("batch_no");
		String payUserId = p.s("pay_user_id");
		String pay_user_name = p.s("pay_user_name");
		String pay_account_no = p.s("pay_account_no");
		String success_details = p.s("success_details", null);
		String fail_details = p.s("fail_details", null);
		int transFee = 0;
		List<TransferResponseItem> items = new ArrayList<>();
		int status = Transfer.order_status_success;

		if (success_details != null) {
			String[] details = success_details.split("\\|");
			for (int i = 0; i < details.length; i++) {
				TransferResponseItem item = createItem(details[i]);
				if (item != null) {
					items.add(createItem(details[i]));
				}
			}

		}
		if (fail_details != null) {
			String[] details = fail_details.split("\\|");
			for (int i = 0; i < details.length; i++) {
				TransferResponseItem item = createItem(details[i]);
				if (item != null) {
					items.add(createItem(details[i]));
				}
			}
		}

		return new TransferResponse(orderNumber, status, transFee, items, null,
				null);

	}

	@Override
	public NotifyContent getNotifyContent(TransferResponse transferResponse) {

		if (transferResponse != null) {
			return success;
		} else {
			return failed;
		}
	}

	@Override
	public void transfer(TransferRequest transferRequest) throws ApiException {
		// Date now = new Date();

		// Map<String, Object> request = new HashMap<String, Object>();
		// request.put("service", service_name);
		// request.put("partner", partner);
		// // request.put("partner", "");
		// request.put("_input_charset", input_charset);
		// // request.put("sign_type", sign_type);
		// // request.put("sign", "");
		// request.put("notify_url", this.getNotifyUrl());
		//
		// request.put("account_name", payer_accountname);
		// // request.put("buyer_account_name", payer_account);// 付款帐号别名
		// request.put("email", payer_account);
		// // request.put("extend_param", null);
		// long seq = transferRequest.getSeq();
		// double newFee = transferRequest.getFee() / 100;// 换成元
		// StringBuffer sb = new StringBuffer();
		// sb.append(seq).append(SP);
		// sb.append(transferRequest.getPayeeAccount()).append(SP);
		// sb.append(transferRequest.getPayeeAccountName()).append(SP);
		// sb.append(newFee).append(SP);
		// sb.append(demo);
		// sb.append("|");
		// request.put("detail_data", sb.toString());
		// String orderNumber = transferRequest.getOrderNumber();
		// // 批量付款批次号
		// request.put("batch_no", orderNumber);
		// // 付款总笔数
		// request.put("batch_num", 1);
		// request.put("batch_fee", newFee);
		// request.put("pay_date",
		// DateUtil.formatDate(transferRequest.getOrderTime(), "yyyyMMdd"));
		//
		// String sign = buildSign(request, sign_type);
		// request.put("sign", sign);
		// request.put("sign_type", sign_type);
		// try {
		// String response = HttpUtil.get(ALIPAY_GATEWAY_NEW, request);
		// System.out.println(response);
		//
		// } catch (Exception e) {
		// throw new ApiException(ErrorCode.INTERNAL_EXCEPTION, "转帐失败", e);
		//
		// }

	}

}
