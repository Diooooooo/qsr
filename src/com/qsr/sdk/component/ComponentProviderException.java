package com.qsr.sdk.component;

import com.qsr.sdk.exception.ApiException;

public class ComponentProviderException extends ApiException {

	/**   */
	private static final long serialVersionUID = -8472784202096839751L;

	private final String providerName;

	public ComponentProviderException(String providerName, int code,
									  String message, Throwable t) {
		super(code, message, t);
		this.providerName = providerName;
		// TODO Auto-generated constructor stub
	}

	public ComponentProviderException(String providerName, int code,
									  String message) {
		super(code, message);
		this.providerName = providerName;
		// TODO Auto-generated constructor stub
	}

	public String getProviderName() {
		return providerName;
	}

}
