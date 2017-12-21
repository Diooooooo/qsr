package com.qsr.sdk.component.payment;

public class NotifyContent {

	private final String content;
	private final String contentType;

	public NotifyContent(String content, String contentType) {
		super();
		this.content = content;
		this.contentType = contentType;

	}

	public NotifyContent(String content) {
		this.contentType = "text/plain; charset=utf-8";
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getContentType() {
		return contentType;
	}

}
