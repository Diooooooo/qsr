package com.qsr.sdk.controller.render.datarender;

public class Status {

	public final static Status STATUS_SUCCESS = new Status();

	private final boolean success;
	private final String message;
	private final int code;

	public Status(int code, String message) {
		this.message = message;
		this.code = code;
		success = false;
	}

	public Status() {
		success = true;
		this.code = 0;
		this.message = null;
	}

	public Status(String message) {
		success = true;
		this.code = 0;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

}
