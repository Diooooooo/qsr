package com.qsr.sdk.component.transfer.provider;

import com.qsr.sdk.component.transfer.*;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.helper.NumberPool;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.Md5Util;
import com.qsr.sdk.util.UrlUtil;

import java.util.Map;

/**
 * 
 * 转帐
 * 
 * @author yuan
 *
 */
public abstract class AbstractTransfer implements Transfer {

	private static final String TRANSACTIONNUMBER_SIGNATUREKEY = "_e9w_trans";

	private final String numberPoolId;

	//private final int providerId;
	TransferProvider provider;

	protected AbstractTransfer(TransferProvider provider, String numberPoolId) {
		this.provider = provider;
		this.numberPoolId = numberPoolId;
	}

	public TransferProvider getProvider() {
		return provider;
	}

	protected long createTransactionSeq() {
		return NumberPool.nextLong(numberPoolId);
	}

	protected String createTransactionNumber() {
		long seq = createTransactionSeq();
		return createTransactionNumber(seq);
	}

	protected String createTransactionNumber(Object seq) {
		StringBuffer sb = new StringBuffer();
		sb.append(seq);
		sb.append(":");
		sb.append(TRANSACTIONNUMBER_SIGNATUREKEY);
		return Md5Util.digest(sb.toString());
	}

	@Override
	public abstract int calcFee(int totalFee) throws ApiException;

	@Override
	public String getNotifyUrl() {
		return UrlUtil.getUrl(Env.getHostUrl(), "api/transfer_notify/callback/"
				+ this.getProvider().getProviderId());
	}

	@Override
	public abstract TransferRequest transferRequest(String payeeAccount,
													String payeeAccountName, int actualFee) throws ApiException;

	@Override
	public abstract void transfer(TransferRequest request) throws ApiException;

	@Override
	public abstract TransferResponse handleNotify(
			Map<String, String> notifyParams) throws ApiException;

	@Override
	public abstract NotifyContent getNotifyContent(
			TransferResponse transferResponse);

}
