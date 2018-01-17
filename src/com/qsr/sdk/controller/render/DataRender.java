package com.qsr.sdk.controller.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.qsr.sdk.controller.render.datarender.Result;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DataRender extends Render {

	private final Result result;

	/**
	 * http://zh.wikipedia.org/zh/MIME 在wiki中查到:
	 * 尚未被接受为正式数据类型的subtype，可以使用x-开始的独立名称（例如application/x-gzip） 所以以下可能要改成
	 * application/x-json
	 * 
	 * 通过使用firefox测试,struts2-json-plugin返回的是 application/json, 所以暂不改为
	 * application/x-json 1: 官方的 MIME type为application/json, 见
	 * http://en.wikipedia.org/wiki/MIME_type 2: IE 不支持 application/json, 在 ajax
	 * 上传文件完成后返回 json时 IE 提示下载文件
	 */
	private static final String contentType = "application/json; charset="
			+ getEncoding();
	private static final String contentTypeForIE = "text/html; charset="
			+ getEncoding();
	private boolean forIE = false;

	public DataRender forIE() {
		forIE = true;
		return this;
	}

	@Override
	public void render() {
		String jsonText = JsonUtil.toJson(result);
		PrintWriter writer = null;
		try {
			response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might  not implement  Cache-Control and  might only implement  Pragma: no-cache
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Access-Control-Allow-Origin", "*");
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

	public Result getResult() {
		return result;
	}

	public DataRender(Object object, String statusMessage) {

		if (object instanceof ApiException) {
			result = new Result(((ApiException) object).getCode(),
					((ApiException) object).getMessage());
		} else if (object instanceof Throwable) {
			result = new Result(ErrorCode.UNKNOW_ERROR,
					((Throwable) object).getMessage());
		} else if (object instanceof PageList<?>) {
			result = new Result((PageList<?>) object, statusMessage);
		} else if (object instanceof List<?>) {
			result = new Result((List<?>) object, statusMessage);
		} else {
			result = new Result(object, statusMessage);
		}
	}

	public DataRender(Object object) {
		this(object, null);
	}

	public DataRender() {
		this(null, null);
	}
}
