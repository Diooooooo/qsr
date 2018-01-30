package com.qsr.sdk.component.transfer.provider.wxtransfer;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.transfer.Transfer;
import com.qsr.sdk.component.transfer.TransferProvider;

import java.util.Map;

public class WxTransferProvider extends AbstractProvider<Transfer> implements TransferProvider {
    public static final int PROVIDER_ID = 2;

    public WxTransferProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public Transfer createComponent(int configId, Map<?, ?> config) {
        return new WxTransfer(this);
    }
}
