package com.qsr.sdk.component.payment.provider.yhxf;

import com.qsr.sdk.component.payment.*;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.HttpUtil;
import com.qsr.sdk.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 深圳盈华讯方
 * 
 * @author yuan
 *
 */
public class YhxfPayment extends AbstractPayment {

	final static Logger logger = LoggerFactory.getLogger(YhxfPayment.class);

	public static final int PROVIDER_ID = 10;

	private static final String sp_id = "30453";
	private static final String sp_pwd = "69d38f5a15ee4fe797";

	private static final String url = "http://ydzf.vnetone.com/sdk/APP/Order.aspx";

	public YhxfPayment(PaymentProvider provider) {
		super("yhxf_payment_seq", provider);
	}

	// @Override
	// protected String createOrderNumber() {
	// // TODO Auto-generated method stub
	//
	// long seq = this.createPaymentSeq();
	// StringBuffer sb = new StringBuffer();
	// sb.append(seq);
	//
	// String code = Md5.digest(sb.toString());
	// code = code.substring(4);
	// return code;// super.createOrderNumber(code);
	// }

	@Override
	public PaymentOrder request(String paymentType, int fee, String clientIp,
                                Map<String, ?> req, String notifyUrl) throws PaymentException {

		try {

			Parameter p = new Parameter(req);
			int fee2 = p.i("mz");
			PaymentOrder result = super.request(paymentType, fee2 * 100,
					clientIp, req, notifyUrl);

			String uid = p.s("uid", "1");
			String cellphone = p.s("mob");
			// String sign = p.stringParam("md5");
			// String oid = result.getOrderNumber(); modify by fengchuang
			// 使用sdk提供的订单号
			String oid = p.s("oid");
			String imsi = p.s("imsi", null);
			String imei = p.s("imei", null);

			String sp_req_url = "";
			String sp_suc_url = this.getNotifyUrl();

			String sp_custom_data = result.getOrderNumber();// "custom";
			StringBuffer sb = new StringBuffer();
			sb.append(sp_id);
			sb.append(oid);
			sb.append(sp_pwd);
			sb.append(fee2);
			sb.append(sp_req_url);
			sb.append(sp_suc_url);
			sb.append(cellphone);
			String sign2 = Md5Util.digest(sb.toString()).toUpperCase();

			Map<String, Object> urlparameter = new HashMap<String, Object>();

			urlparameter.put("sp", sp_id);
			urlparameter.put("od", oid);
			urlparameter.put("mz", fee2);
			urlparameter.put("md5", sign2);
			urlparameter.put("spreq", sp_req_url);
			urlparameter.put("spsuc", sp_suc_url);
			urlparameter.put("spzdy", sp_custom_data);
			urlparameter.put("mob", cellphone);
			urlparameter.put("uid", uid);
			urlparameter.put("imsi", imsi);
			urlparameter.put("imei", imei);

			logger.debug("支付请求信息:" + urlparameter);

			String returnString = HttpUtil.post(url, urlparameter);// 调用支付平台

			result.setPaymentCode(returnString);
			// TODO 测试
			// result.setPaymentCode("yhxffail|该手机号下单失败");

			logger.debug("支付返回信息:" + returnString);

			return result;
		} catch (ApiException e) {
			throw new PaymentException(e);
		} catch (IOException e) {
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"第三方服务网络错误", e);
		}
	}

	@Override
	protected boolean verifyData(Map<String, ?> resp) {

		// add by fengchuang 添加校验逻辑
		// 验证加密方式
		Parameter p = new Parameter(resp);

		String md5 = p.s("md5", "");

		String oid = p.s("oid", "");
		String sporder = p.s("sporder", "");
		String spid = p.s("spid", "");
		String mz = p.s("mz", "");
		String sppwd = sp_pwd;

		StringBuffer sb = new StringBuffer();
		sb.append(oid);
		sb.append(sporder);
		sb.append(spid);
		sb.append(mz);
		sb.append(sppwd);
		String sign2 = Md5Util.digest(sb.toString()).toUpperCase();

		return md5.equals(sign2);
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return PaymentStatus.PaySuccess;
	}

	@Override
	protected PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {
		// TODO Auto-generated method stub
		try {
			Parameter p = new Parameter(resp);

			String sp_id = p.s("spid", "");
			String sgin = p.s("md5", "");
			String oid = p.s("oid", "");
			String orderNumber = p.s("sporder", "");
			String mzStr = p.s("mz", "");
			int fee = 0;
			if (mzStr != null && !"".equals(mzStr)) {
				fee = p.i("mz") * 100;
			}
			String customData = p.s("zdy", "");
			String spUid = p.s("spuid", "");
			PaymentStatus status = getPaymentStatus(null);

			return new PaymentResponse(orderNumber, oid, status, fee, null,
					null);
		} catch (IllegalArgumentException e) {

			throw new PaymentException(ErrorCode.ILLEGAL_ARGUMENT,
					e.getMessage(), e);
		}
	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {

		// TODO 如果成功返回okydzf,否则返回failydzf modify by fengchuang 2015-04-22
		if (paymentResult != null) {
			return new NotifyContent("okydzf");
		} else {
			return new NotifyContent("failydzf");
		}
	}

}
