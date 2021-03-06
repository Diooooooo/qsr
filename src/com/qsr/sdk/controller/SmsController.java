package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.SmsService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.Constants;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.LoggerFactory;

public class SmsController extends WebApiController {

	final static org.slf4j.Logger logger = LoggerFactory.getLogger(SmsController.class);

	public SmsController() {
		super(logger);
	}

	public void verifySms() {
		try {
			Fetcher f = this.fetch();
			logger.debug("verify,params={}", f);
			String phoneNumber = f.s("phone_number");
			String verifyCode = f.s("verify_code");
			SmsService smsService = this.getService(SmsService.class);
			smsService.verifyCode(phoneNumber, verifyCode);
			this.renderData(SUCCESS);
		} catch (Throwable t) {
			this.renderException("verify", t);
		}
	}

	public void getRegisterVerifyCode() {
		try {
		    Fetcher f = this.fetch();
			logger.debug("getRegisterVerifyCode,params={}", f.getParameterMap());
			String phoneNumber = f.s("phone_number");
            UserService userService = this.getService(UserService.class);
            if (userService.check(phoneNumber)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "已注册的手机号");
            }
			this.getService(SmsService.class).getVerifyCode(Constants.SMS_TYPE_REGISTER, phoneNumber);
			this.renderData(SUCCESS);
		} catch (Throwable e) {
			this.renderException("getRegisterVerifyCode", e);
		}
	}

	public void getLoginVerifyCode() {
		try {
		    Fetcher f = this.fetch();
			logger.debug("getLoginVerifyCode,params={}", f.getParameterMap());
			SmsService smsService = this.getService(SmsService.class);
			smsService.getVerifyCode(Constants.SMS_TYPE_LOGIN, f.s("phone_number"));
			this.renderData(SUCCESS);
		} catch (Throwable e) {
			this.renderException("getLoginVerifyCode", e);
		}
	}

	public void getResetPwdVerifyCode() {
		try {
		    Fetcher f = this.fetch();
			logger.debug("getResetPwdVerifyCode,params={}", f);
			this.getService(SmsService.class).getVerifyCode(Constants.SMS_TYPE_RESET_PWD, f.s("phone_number"));
			this.renderData(SUCCESS);
		} catch (Throwable e) {
			this.renderException("getResetPwdVerifyCode", e);
		}
	}
}
