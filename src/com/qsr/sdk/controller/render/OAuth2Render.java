package com.qsr.sdk.controller.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.qsr.sdk.service.exception.OAuth2ServiceException;
import com.qsr.sdk.util.HttpUtil;
import com.qsr.sdk.util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class OAuth2Render extends Render {

	private OAuth2ServiceException ex;

	private String redirectUrl;
	private Map<?, ?> params;
	private boolean fragment = false;
	private boolean json = true;
	private static final String contentType = "application/json; charset="
			+ getEncoding();
	private static final String contentTypeForIE = "text/html; charset="
			+ getEncoding();
	private boolean forIE = false;

	public OAuth2Render(OAuth2ServiceException ex) {
		this.ex = ex;
		this.params = ex.getParameters();
	}

	public OAuth2Render(String redirectUrl, Map<?, ?> params) {
		this(redirectUrl, params, true, false);
	}

	public OAuth2Render(String redirectUrl, Map<?, ?> params, boolean json,
			boolean fragment) {
		this.redirectUrl = redirectUrl;
		this.params = params;
		this.fragment = fragment;
		this.json = json;

	}

	protected void renderContent() {
		if (params != null) {
			response.setContentType(forIE ? contentTypeForIE : contentType);
			String jsonText = JsonUtil.toJson(params);
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.write(jsonText);
				writer.flush();
			} catch (IOException e) {

			} finally {
				if (writer != null)
					writer.close();
			}
		}
	}

	@Override
	public void render() {

		response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might  not implement  Cache-Control and  might only implement  Pragma: no-cache
		response.setHeader("Cache-Control", "no-cache");
		//response.setDateHeader("Expires", 0);

		if (ex != null) {
			renderContent();
			this.response.setStatus(ex.getResponseCode());
			for (Map.Entry<String, String> entry : ex.getHeaders().entrySet()) {
				this.response.setHeader(entry.getKey(), entry.getValue());
			}

		} else {

			String url = HttpUtil.urlencoded(redirectUrl, params, fragment,
					null);
			if (json) {
				renderContent();

			} else {
				try {
					response.sendRedirect(url); // always 302
				} catch (IOException e) {
					throw new RenderException(e);
				}
			}

		}
	}

}
