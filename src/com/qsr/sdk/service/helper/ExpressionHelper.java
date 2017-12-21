package com.qsr.sdk.service.helper;

import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.expression.Expression;
import com.qsr.sdk.component.expression.ExpressionManager;
import com.qsr.sdk.util.ErrorCode;

public class ExpressionHelper {

	static ExpressionManager expressionManger;
	static {
		expressionManger = ComponentProviderManager.getService(
				ExpressionManager.class, 1, 1);
	}

	//	protected static List<Function> classToFunctionList(String namespace,
	//			String className) throws Exception {
	//
	//		List<Function> list = new ArrayList<>();
	//		Class<?> clazz = Class.forName(className);
	//		Object object = clazz.newInstance();
	//		Method[] methods = clazz.getMethods();
	//
	//		for (Method method : methods) {
	//			//m.getParameterTypes();
	//			JavaFunction function = new JavaFunction(namespace, method, object);
	//			list.add(function);
	//		}
	//		return list;
	//
	//	}

	//	public static void loadFunctions() {
	//		List<Function> functions = new ArrayList<>();
	//		//buildin functions
	//		functions.add(new Md5());
	//		functions.add(new Regex());
	//
	//		filterManger.registerFunction(functions);
	//
	//	}

	public static ExpressionManager getFilterManager() {
		return expressionManger;
	}

	public static Expression getExpression(int providerId, int configId,
			String expressionContent) throws ApiException {

		ExpressionManager expressionManger = ComponentProviderManager.getService(
				ExpressionManager.class, providerId, configId);

		if (expressionManger == null) {
			throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
					"不存在的表达式服务");
		}

		return expressionManger.getExpression(expressionContent,
				Expression.TYPE_EXPRESSION);

	}
}
