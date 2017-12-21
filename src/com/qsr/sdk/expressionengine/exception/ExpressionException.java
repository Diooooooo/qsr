package com.qsr.sdk.expressionengine.exception;

public class ExpressionException extends RuntimeException {

	/**   */
	private static final long serialVersionUID = 7429363202202908664L;

	public ExpressionException() {
		super();
	}

	public ExpressionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressionException(String message) {
		super(message);
	}

	public ExpressionException(Throwable cause) {
		super(cause);
	}

}
