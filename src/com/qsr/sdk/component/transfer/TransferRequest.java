package com.qsr.sdk.component.transfer;

public class TransferRequest {

	private final int fee;
	// private final int currencyType;
	// private final int currencyAmount;

	private final String payeeAccount;
	private final String payeeAccountName;

	public TransferRequest(int fee, String payeeAccount, String payeeAccountName) {
		super();

		this.fee = fee;

		this.payeeAccount = payeeAccount;
		this.payeeAccountName = payeeAccountName;
	}

	public int getFee() {
		return fee;
	}

	public String getPayeeAccount() {
		return payeeAccount;
	}

	public String getPayeeAccountName() {
		return payeeAccountName;
	}

}
