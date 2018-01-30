package com.qsr.sdk.component.transfer.provider.wxtransfer;

import com.qsr.sdk.component.transfer.NotifyContent;
import com.qsr.sdk.component.transfer.TransferProvider;
import com.qsr.sdk.component.transfer.TransferRequest;
import com.qsr.sdk.component.transfer.TransferResponse;
import com.qsr.sdk.component.transfer.provider.AbstractTransfer;
import com.qsr.sdk.exception.ApiException;

import java.util.Map;

public class WxTransfer extends AbstractTransfer {
    protected WxTransfer(TransferProvider provider) {
        super(provider, "wx_transfer_seq");
    }

    @Override
    public int calcFee(int totalFee) throws ApiException {
        return 0;
    }

    @Override
    public TransferRequest transferRequest(String payeeAccount, String payeeAccountName, int actualFee) throws ApiException {
        return null;
    }

    @Override
    public void transfer(TransferRequest request) throws ApiException {

    }

    @Override
    public TransferResponse handleNotify(Map<String, String> notifyParams) throws ApiException {
        return null;
    }

    @Override
    public NotifyContent getNotifyContent(TransferResponse transferResponse) {
        return null;
    }
}
