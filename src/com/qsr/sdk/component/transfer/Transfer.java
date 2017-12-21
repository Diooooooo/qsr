package com.qsr.sdk.component.transfer;

import com.qsr.sdk.component.Component;
import com.qsr.sdk.exception.ApiException;

import java.util.Map;

public interface Transfer extends Component {

	public static final int order_status_create = 0;
	public static final int order_status_submitsuccess = 1;
	public static final int order_status_submitfailed = 2;
	public static final int order_status_success = 3;
	public static final int order_status_failed = 4;

	public static final int request_status_create = 0;
	public static final int request_status_accepted = 10;
	public static final int request_status_failed = 15;
	public static final int request_status_success = 20;

	public abstract int calcFee(int totalFee) throws ApiException;

	public abstract String getNotifyUrl();

	public abstract TransferRequest transferRequest(String payeeAccount,
			String payeeAccountName, int actualFee) throws ApiException;

	public abstract void transfer(TransferRequest request) throws ApiException;

	public abstract TransferResponse handleNotify(
			Map<String, String> notifyParams) throws ApiException;

	public abstract NotifyContent getNotifyContent(
			TransferResponse transferResponse);

}