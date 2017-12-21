package com.qsr.sdk.component.transfer.provider.alitransfer;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.transfer.Transfer;
import com.qsr.sdk.component.transfer.TransferProvider;

import java.util.Map;

public class AliTransferProvider extends AbstractProvider<Transfer> implements
		TransferProvider {

	public static final int PROVIDER_ID = 1;

	Transfer transfer;

	public AliTransferProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public Transfer createComponent(int configId, Map<?, ?> config) {
		return new AliTransfer(this);
	}

}
