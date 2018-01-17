package com.qsr.sdk.component.im;

import com.qsr.sdk.component.Provider;

public interface ImProvider extends Provider {

    Im getComponent(int configId);

}
