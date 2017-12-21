package com.qsr.sdk.component.transfer;

import java.util.List;

public class TransferResponse {

	final String orderNumber;
	final int orderStatus;
	final int transferFee;
	final String returnCode;
	final String returnMessage;

	private final List<TransferResponseItem> items;

	public TransferResponse(String orderNumber, int orderStatus,
			int transferFee, List<TransferResponseItem> items,
			String returnCode, String returnMessage) {
		super();
		this.orderNumber = orderNumber;
		this.orderStatus = orderStatus;
		this.transferFee = transferFee;
		this.returnCode = returnCode;
		this.returnMessage = returnMessage;
		this.items = items;

	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public int getTransferFee() {
		return transferFee;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public List<TransferResponseItem> getItems() {
		return items;
	}

}
