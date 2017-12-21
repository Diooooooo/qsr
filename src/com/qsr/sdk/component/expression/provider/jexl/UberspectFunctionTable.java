package com.qsr.sdk.component.expression.provider.jexl;

import org.apache.commons.jexl2.JexlInfo;
import org.apache.commons.jexl2.introspection.JexlMethod;
import org.apache.commons.jexl2.introspection.UberspectImpl;
import org.apache.commons.logging.Log;

public class UberspectFunctionTable extends UberspectImpl {

	public UberspectFunctionTable(Log runtimeLogger) {
		super(runtimeLogger);
	}

	@Override
	public JexlMethod getMethod(Object obj, String method, Object[] args,
			JexlInfo info) {
		if (obj instanceof JexlFunctionTable) {
			JexlFunctionTable table = (JexlFunctionTable) obj;
			return table.get(method);
		} else {
			return super.getMethod(obj, method, args, info);
		}
	}

}
