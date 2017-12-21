package com.qsr.sdk.component.transfer;

public class TransferResponseItem {

	private final boolean success;
	private final int itemId;
	private final String payeeAccount;
	private final String payeeAccountName;
	private final String reason;
	private final String internalId;
	private final String time;
	private final int fee;

	public TransferResponseItem(boolean success, int itemId,
			String payeeAccount, String payeeAccountName, int fee,
			String reason, String internalId, String time) {
		super();
		this.success = success;
		this.itemId = itemId;
		this.payeeAccount = payeeAccount;
		this.payeeAccountName = payeeAccountName;
		this.reason = reason;
		this.internalId = internalId;
		this.time = time;
		this.fee = fee;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getItemId() {
		return itemId;
	}

	public String getPayeeAccount() {
		return payeeAccount;
	}

	public String getPayeeAccountName() {
		return payeeAccountName;
	}

	public String getReason() {
		return reason;
	}

	public String getInternalId() {
		return internalId;
	}

	public String getTime() {
		return time;
	}

	public int getFee() {
		return fee;
	}

}
