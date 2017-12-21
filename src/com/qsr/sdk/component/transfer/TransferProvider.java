package com.qsr.sdk.component.transfer;

import com.qsr.sdk.component.Provider;

public interface TransferProvider extends Provider {

	public Transfer getComponent(int configId);

}
