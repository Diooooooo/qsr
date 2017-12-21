package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushController extends WebApiController {
	final static org.slf4j.Logger logger = LoggerFactory.getLogger(PushController.class);

	public PushController() {
		super(logger);
	}

	public void sendMessage() {
		try {
			Fetcher f = this.fetch();
			logger.debug("sendMessage,params={}", f);

			int userId = f.i("user_id");
			int type = f.i("type", 1);
			String message = f.s("message");

			PushService pushService = this.getService(PushService.class);
			pushService.pushMessage(userId, type, message);

			this.renderData();
		} catch (Throwable t) {
			this.renderException("sendMessage", t);
		}
	}
}
