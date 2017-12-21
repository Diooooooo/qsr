package com.qsr.sdk.util;

import java.util.HashMap;
import java.util.Map;

public class TemplateUtil {

	public static String process(String template, Map<?, ?> variants) {

		String result = StringUtil.getEmptyString(template);
		for (Map.Entry<?, ?> entry : variants.entrySet()) {
			if (entry.getValue() != null) {
				result = result.replace("{" + entry.getKey() + "}", entry
						.getValue().toString());
			}
		}

		return result;
	}

	public static String process(Map<?, ?> variants, String templateField) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.putAll(variants);
		String template = (String) map.remove(templateField);
		if (StringUtil.isEmptyOrNull(template)) {
			return StringUtil.EMPTY_STRING;
		}

		return process(template, map);
	}
}
