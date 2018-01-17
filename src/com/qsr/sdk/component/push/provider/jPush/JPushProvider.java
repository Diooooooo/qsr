package com.qsr.sdk.component.push.provider.jPush;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.component.push.PushProvider;

import java.util.Map;

public class JPushProvider extends AbstractProvider<Push> implements PushProvider {

    private static final int PROVIDER_ID = 2;

    public JPushProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public Push createComponent(int configId, Map<?, ?> config) {
        return new JPush(this, config);
    }
}
