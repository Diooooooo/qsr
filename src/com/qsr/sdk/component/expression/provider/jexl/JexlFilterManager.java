package com.qsr.sdk.component.expression.provider.jexl;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.expression.Expression;
import com.qsr.sdk.component.expression.ExpressionManager;
import com.qsr.sdk.component.expression.Function;
import org.apache.commons.jexl2.JexlArithmetic;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.Script;
import org.apache.commons.jexl2.introspection.Uberspect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JexlFilterManager extends AbstractComponent implements
        ExpressionManager {

	protected final Log log = LogFactory.getLog(JexlFilterManager.class);

	//	protected JexlEngine jexlEngine;
	protected volatile Map<String, Object> theFunctions;

	//	protected Uberspect uberspect;
	//	protected JexlArithmetic arithmetic;

	public JexlFilterManager(Provider provider) {
		super(provider);
		theFunctions = new ConcurrentHashMap<String, Object>();
	}

	@Override
	public Expression getExpression(String content, int filterType) {
		Uberspect uberspect = new UberspectFunctionTable(log);
		JexlArithmetic arithmetic = new JexlArithmetic(true);
		JexlEngine jexlEngine = new JexlEngine(uberspect, arithmetic,
				theFunctions, log);
		jexlEngine.setCache(512);

		Expression filter;
		if (Expression.TYPE_EXPRESSION == filterType) {
			org.apache.commons.jexl2.Expression expression = jexlEngine
					.createExpression(content);
			filter = new JexlExpressionFilter(expression);
		} else {
			Script script = jexlEngine.createScript(content);
			filter = new JexlScriptFilter(script);
		}

		return filter;
	}

	@Override
	public void registerFunction(List<Function> list) {

		Map<String, Object> functions = new ConcurrentHashMap<String, Object>();

		for (Function funcition : list) {
			JexlFunctionTable functionTable;
			Object value = functions.get(funcition.getNamespace());
			if (value instanceof JexlFunctionTable) {
				functionTable = (JexlFunctionTable) value;
			} else {
				functionTable = new JexlFunctionTable();
				functions.put(funcition.getNamespace(), functionTable);
			}
			functionTable.put(new JexlFunction(funcition));
		}
		theFunctions = functions;
	}

	@Override
	public void clearFunctions() {
		theFunctions = new ConcurrentHashMap<String, Object>();
	}

}
