package com.qsr.sdk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Map;

public class HomeController extends WebApiController {
	final static Logger logger = LoggerFactory.getLogger(HomeController.class);

	public HomeController() {
		super(logger);
		// TODO Auto-generated constructor stub
	}

	public void index() {

		String remoteAddr = this.getRemoteAddr();
		String realRemoteAddr = this.getRealRemoteAddr();
		String userAgent = this.getUserAgent();

		StringBuffer stringBuffer=new StringBuffer("welcome to qsr").append("\r\n");
		stringBuffer.append("\r\n").append("remoteAddr=").append(remoteAddr);
		stringBuffer.append("\r\n").append("realRemoteAddr=").append(realRemoteAddr);
		stringBuffer.append("\r\n").append("userAgent=").append(userAgent).append("\r\n");

		stringBuffer.append("\r\n").append("header--------------------");

		for (Map.Entry<String, String> entry : this.getHeaders().entrySet()) {
			stringBuffer.append("\r\n").append(entry.getKey()).append("=").append(entry.getValue());
		}

		stringBuffer.append("\r\n").append("version=").append(System.getenv("api_instance_id"));
		this.renderText(stringBuffer.toString());
	}

	private static Charset charset;
	static {
		charset = Charset.forName("UTF-8");
	}

}
