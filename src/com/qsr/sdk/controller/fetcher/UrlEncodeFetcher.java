package com.qsr.sdk.controller.fetcher;

import com.jfinal.core.Controller;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UrlEncodeFetcher extends Fetcher {

	protected final static Charset CHARSET_DEFAULT = Charset
			.forName("ISO-8859-1");

	public UrlEncodeFetcher(Controller controller) {
		super(controller);
	}

	@Override
	protected Map<String, Object> buildParameterMap() {
		Map<String, Object> parameterMap = new HashMap<>();
		Map<String, String[]> requestParams = this.getController().getParaMap();
		String[] charsetName = requestParams.get("charset");
		Charset charset = null;
		if (charsetName != null && charsetName.length > 0) {
			try {
				charset = Charset.forName(charsetName[0]);
			} catch (Throwable e) {
			}
		}
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter
				.hasNext();) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			if (charset != null) {
				valueStr = new String(valueStr.getBytes(CHARSET_DEFAULT),
						charset);
			}
			parameterMap.put(name, valueStr);
		}
		return parameterMap;
	}

}
