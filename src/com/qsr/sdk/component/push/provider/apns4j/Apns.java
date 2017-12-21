package com.qsr.sdk.component.push.provider.apns4j;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.component.push.provider.apns4j.impl.ApnsServiceImpl;
import com.qsr.sdk.component.push.provider.apns4j.model.ApnsConfig;
import com.qsr.sdk.component.push.provider.apns4j.model.Payload;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.WorkingResourceUtil;

import java.io.InputStream;

public class Apns extends AbstractComponent implements Push {

	public static final int PROVIDER_ID = 2;

	private IApnsService apnsService;

	public Apns(ApnsPushProvider provider) {
		super(provider);
		ApnsConfig config = new ApnsConfig();
		InputStream is = WorkingResourceUtil.getInputStream("apns.p12");
		config.setKeyStore(is);
		// config.setDevEnv(true);
		config.setDevEnv(false);
		config.setPassword("19where");
		config.setPoolSize(5);
		apnsService = ApnsServiceImpl.createInstance(config);
	}

	@Override
	public int pushSingleMessage(String userId, String message, int type)
			throws ApiException {
		Payload payload = new Payload();
		payload.setAlert(message);
		apnsService.sendNotification(userId, payload);
		// payload.setBadge(1);
		// apnsService.sendNotification(userId, payload);
		return 0;

	}

}
