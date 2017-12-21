package com.qsr.sdk.component.expression;

import com.qsr.sdk.component.Component;

import java.util.List;

public interface ExpressionManager extends Component {

	public Expression getExpression(String content, int filterType);

	public void registerFunction(List<Function> list);

	public void clearFunctions();

}
