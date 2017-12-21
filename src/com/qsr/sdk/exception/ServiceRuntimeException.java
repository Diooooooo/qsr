package com.qsr.sdk.exception;

public class ServiceRuntimeException extends RuntimeException {

	/**   */
	private static final long serialVersionUID = -4329836812530379389L;

	ServiceRuntimeException() {
		super();
	}

	ServiceRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	ServiceRuntimeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	ServiceRuntimeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	ServiceRuntimeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
