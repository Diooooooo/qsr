package com.qsr.sdk.component.sms;

import com.qsr.sdk.component.Component;
import com.qsr.sdk.exception.ApiException;

import java.util.Map;

public interface SmsSend extends Component {

	public SendResult send(String phoneNumber, String template, Map<String, String> templateParams) throws ApiException;

	public VerifyResult verify(String phoneNumber, String verifyCode);

	//	public Provider getProvider();
}
