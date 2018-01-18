package com.qsr.sdk.component.im.provider;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.im.Im;
import com.qsr.sdk.component.im.ImProvider;

import java.util.Map;

public class JImProvider extends AbstractProvider<Im> implements ImProvider {
    private static final int PROVIDER_ID = 1;

    public JImProvider() {
        super(PROVIDER_ID);
    }

    public JIm createComponent(int configId, Map<?, ?> config) {
        return new JIm(this, config);
    }
}
