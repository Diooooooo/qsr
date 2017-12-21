package com.qsr.sdk.component.expression.provider.jexl;

import com.qsr.sdk.component.expression.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.Script;

import java.util.Map;

public class JexlScriptFilter implements Expression {

	private final Script script;

	public JexlScriptFilter(Script script) {
		this.script = script;
	}

	@Override
	public Object evaluate(Map<String, Object> context) {
		JexlContext jexlContent = new MapContext(context);
		return script.execute(jexlContent);

	}

	@Override
	public int getFilterType() {
		return Expression.TYPE_SCRIPT;
	}

}
