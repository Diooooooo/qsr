package com.qsr.sdk.service.exception;


import com.qsr.sdk.util.ErrorCode;

import java.util.LinkedHashMap;
import java.util.Map;

public class OAuth2ServiceException extends ServiceException {

	/**   */
	private static final long serialVersionUID = 982132791235349965L;

	private Map<String, Object> parameters = new LinkedHashMap<String, Object>();
	private Map<String, String> headers = new LinkedHashMap<String, String>();

	private final int responseCode;

	public OAuth2ServiceException(String serviceName, int responseCode,
			String error, String message, Throwable t) {
		super(serviceName, ErrorCode.OAUTH2_ERROR, message, t);
		this.responseCode = responseCode;
		parameters.put("error", error);
		parameters.put("error_description", message);
	}

	public OAuth2ServiceException(String serviceName, int responseCode,
			String error, String message) {
		this(serviceName, responseCode, error, message, null);
	}

	public OAuth2ServiceException(String serviceName, String error,
			String message, Throwable t) {
		this(serviceName, 400, error, message, t);
	}

	public OAuth2ServiceException(String serviceName, String error,
			String message) {
		this(serviceName, 400, error, message, null);
	}

	public OAuth2ServiceException(String error, String message) {
		this("OAuth2Service", 400, error, message, null);
	}

	public OAuth2ServiceException(String error, Throwable t) {
		this("OAuth2Service", 400, error, t.getMessage(), null);
	}

	//	public OAuth2ServiceException responseCode(){
	//		return this;
	//	}

	public OAuth2ServiceException description(String description) {
		parameters.put("error_description", description);

		return this;
	}

	public OAuth2ServiceException uri(String uri) {
		parameters.put("error_uri", uri);
		return this;
	}

	public OAuth2ServiceException state(String state) {
		parameters.put("state", state);
		return this;
	}

	public OAuth2ServiceException scope(String scope) {
		parameters.put("scope", scope);
		return this;
	}

	public OAuth2ServiceException setParameter(String name, String value) {
		parameters.put(name, value);
		return this;
	}

	public OAuth2ServiceException addHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	public Object get(String name) {
		return parameters.get(name);
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public String getMessage() {

		return parameters.toString();
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		return "OAuthProblemException{" + parameters.toString() + '}';
	}

	public int getResponseCode() {
		return responseCode;
	}

}
