package com.qsr.sdk.controller.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.qsr.sdk.util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class FlagRender extends Render {

	private final int flag;
	private final String message;
	private static final String contentType = "application/json; charset="
			+ getEncoding();
	private static final String contentTypeForIE = "text/html; charset="
			+ getEncoding();
	private boolean forIE = false;

	public FlagRender forIE() {
		forIE = true;
		return this;
	}

	@Override
	public void render() {

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("flag", flag);
		data.put("msg", message);

		String jsonText = JsonUtil.toJson(data);

		PrintWriter writer = null;
		try {
			response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might  not implement  Cache-Control and  might only implement  Pragma: no-cache
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);

			response.setContentType(forIE ? contentTypeForIE : contentType);
			writer = response.getWriter();
			writer.write(jsonText);
			writer.flush();
		} catch (IOException e) {
			throw new RenderException(e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public FlagRender(int flag, String message) {
		super();
		this.flag = flag;
		this.message = message;
	}

	public int getFlag() {
		return flag;
	}

	public String getMessage() {
		return message;
	}

}
