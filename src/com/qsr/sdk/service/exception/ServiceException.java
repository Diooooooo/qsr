package com.qsr.sdk.service.exception;

import com.qsr.sdk.exception.ApiException;

public class ServiceException extends ApiException {

	/**   */
	private static final long serialVersionUID = 8912308230848169141L;

	private final String serviceName;

	public ServiceException(String serviceName, ApiException e) {
		this(serviceName, e.getCode(), e.getMessage(), e.getCause());
	}

	public ServiceException(String serviceName, int code, String message,
			Throwable t) {
		super(code, message, t);
		this.serviceName = serviceName;

	}

	public ServiceException(String serviceName, int code, String message) {
		this(serviceName, code, message, null);
	}

	public String getServiceName() {
		return serviceName;
	}

}
