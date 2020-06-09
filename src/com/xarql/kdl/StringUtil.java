package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalObjectName;

public abstract class StringUtil implements CommonNames {

	public static Object handleExpression(kdlParser.ExpressionContext xpr, SourceListener caller) {
		final Operator opr = Operator.match(xpr.operator().getText());
		final InternalObjectName type1 = caller.parseType(xpr.value(0));
		final InternalObjectName type2 = caller.parseType(xpr.value(1));

		if(type1.equals(STRING_ION))
			throw new IllegalArgumentException("StringUtil only handles cases where the first operand is a string");

		if(opr == PLUS) {

		}
		return null;
	}
}
