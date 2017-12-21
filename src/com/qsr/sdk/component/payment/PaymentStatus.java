package com.qsr.sdk.component.payment;

public enum PaymentStatus {

	NotPay(0, false, false), WaitPay(10, false, false), PayFailed(15, false,
			true), PaySuccess(20, true, true);

	private final int statusId;
	private final boolean success;
	private final boolean finished;

	private PaymentStatus(int statusId, boolean success, boolean finished) {
		this.statusId = statusId;
		this.success = success;
		this.finished = finished;
	}

	public int getStatusId() {
		return statusId;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFinished() {
		return finished;
	}

}
