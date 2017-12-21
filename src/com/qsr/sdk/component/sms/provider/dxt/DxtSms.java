package com.qsr.sdk.component.sms.provider.dxt;

import com.qsr.sdk.component.sms.SendResult;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.component.sms.SmsSendProvider;
import com.qsr.sdk.component.sms.VerifyResult;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

import java.util.Map;

public class DxtSms implements SmsSend {

	SmsSendProvider provider;

	public DxtSms(SmsSendProvider provider) {
		//super(PROVIDER_ID);
		this.provider = provider;
	}

	private static String url = "http://124.202.131.5/man/sendMsg";

	private static String[] fill = { "000000", "00000", "0000", "000", "00",
			"0", "" };

	public SendResult send(String phoneNumber) throws ApiException {
		throw new ApiException(ErrorCode.NOT_IMPLEMENTS,
				"暂停服务");
//		try {
//
//			Map<String, Object> request = new HashMap<>();
//			Random rand = new Random();
//			String verifyCode = "" + rand.nextInt(999999);
//			verifyCode = fill[verifyCode.length()] + verifyCode;
//			String message = "此验证码只用于在哎呦内绑定手机号，验证码提供给他人将导致个人信息被盗。" + verifyCode
//					+ "（哎呦验证码）。再次提醒，请勿转发[哎呦小助手]";
//			request.put("phoneNum", phoneNumber);
//			request.put("msg", message);
//			String json = HttpUtil.post(url, request);
//			Map<String, Object> response = JsonUtil.fromJsonToMap(json);
//			Object number = response.get("success");
//			Object m = response.get("message");
//			String responeMessage = m != null ? m.toString() : "";
//			boolean success = false;
//			if (number != null && number instanceof Number) {
//				success = ((Number) number).intValue() == 1;
//			}
//			if (!success) {
//				throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
//						"发送验证码失败:" + responeMessage);
//			}
//
//			SendResult verifyRequest = new SendResult(phoneNumber,
//					message, verifyCode);
//			return verifyRequest;
//		} catch (ApiException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new ApiException(ErrorCode.INTERNAL_EXCEPTION, "发送验证码失败", e);
//		}

	}

	@Override
	public SendResult send(String phoneNumber, String template, Map<String, String> templateParams) throws ApiException {
		throw new ApiException(ErrorCode.NOT_IMPLEMENTS,
				"暂停服务");
//		return null;
	}

	@Override
	public VerifyResult verify(String phoneNumber, String verifyCode) {
		return new VerifyResult(verifyCode);
	}

	@Override
	public SmsSendProvider getProvider() {
		return provider;
	}

}
