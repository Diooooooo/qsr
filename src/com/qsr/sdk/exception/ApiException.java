package com.qsr.sdk.exception;

public class ApiException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4823326887307599284L;
	private final int code;
	private final String message;

	public ApiException(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public ApiException(int code, String message, Throwable t) {
		super(message, t);
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	//	public String getMessage() {
	//		return message;
	//	}

}
