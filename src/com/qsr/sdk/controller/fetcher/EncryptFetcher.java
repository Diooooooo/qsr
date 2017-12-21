package com.qsr.sdk.controller.fetcher;

import com.jfinal.core.Controller;
import com.qsr.sdk.util.DataSecret;
import com.qsr.sdk.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class EncryptFetcher extends UrlEncodeFetcher {

	boolean json = true;
	boolean encrypt = true;

	public EncryptFetcher(Controller controller) {
		super(controller);
	}

	@Override
	protected Map<String, Object> buildParameterMap() {

		if ("0".equals(this.getController().getPara("_json"))) {
			this.json = false;
		}
		if ("0".equals(this.getController().getPara("_encrypt"))) {
			encrypt = false;
		}
		if (json) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			String signature = this.getController().getPara("signature");
			String dataString = this.getController().getPara("data");

			if (dataString == null || dataString.trim().length() == 0) {
				throw new IllegalArgumentException("缺少参数 :data");
			}

			if (encrypt) {
				try {
					dataString = DataSecret.decryptDES(dataString);
				} catch (Exception e) {
					throw new IllegalStateException("解密数据异常", e);
				}
			}
			parameterMap = JsonUtil.fromJsonToMap(dataString);

			parameterMap.put("signature", signature);

			return parameterMap;
		} else {
			return super.buildParameterMap();
		}

	}

}
