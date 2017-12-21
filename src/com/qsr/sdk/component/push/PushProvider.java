package com.qsr.sdk.component.push;

import com.qsr.sdk.component.Provider;

public interface PushProvider extends Provider {

	Push getComponent(int configId);
}
