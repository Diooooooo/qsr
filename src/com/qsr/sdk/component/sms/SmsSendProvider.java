package com.qsr.sdk.component.sms;

import com.qsr.sdk.component.Provider;

public interface SmsSendProvider extends Provider {

	SmsSend getComponent(int configId);

}
