package com.qsr.sdk.component.sms;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SendResult {

//	private final String phoneNumber;
//	private final String message;
//	private final String sign;
	private final boolean success;
	private final String code;
	private final String message;
	private final Map<String,String> attributes=new LinkedHashMap<>();

	public SendResult(boolean success, String code, String message, Map<String, String> attributes) {
		this.success = success;
		this.code = code;
		this.message = message;
		if(attributes!=null) {
			this.attributes.putAll(attributes);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}
	//	public SendResult(String phoneNumber, String message, String sign) {
//		super();
//		this.phoneNumber = phoneNumber;
//		this.message = message;
//		this.sign = sign;
//	}
//
//	public String getPhoneNumber() {
//		return phoneNumber;
//	}
//
//	public String getMessage() {
//		return message;
//	}
//
//	public String getSign() {
//		return sign;
//	}

}
