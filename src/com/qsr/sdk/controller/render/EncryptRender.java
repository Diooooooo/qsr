package com.qsr.sdk.controller.render;

import com.jfinal.render.RenderException;
import com.qsr.sdk.util.DataSecret;
import com.qsr.sdk.util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;

public class EncryptRender extends DataRender {

	private final boolean encrypt;

	public EncryptRender(boolean encrypt, Object object) {
		super(object);
		this.encrypt = encrypt;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	private static final String contentType1 = "text/html; charset="
			+ getEncoding();
	private static final String contentType2 = "application/json; charset="
			+ getEncoding();

	@Override
	public void render() {
		String content = JsonUtil.toJson(this.getResult());
		PrintWriter writer = null;
		try {
			response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might  not implement  Cache-Control and  might only implement  Pragma: no-cache
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			if (encrypt) {
				content = DataSecret.encryptDES(content);
			}
			response.setContentType(encrypt ? contentType1 : contentType2);
			writer = response.getWriter();
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			throw new RenderException(e);
		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
