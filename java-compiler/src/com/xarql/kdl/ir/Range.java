package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.antlr.kdl;

public class Range {
	public static final int DEFAULT_MIN = 0;

	public final Pushable min;
	public final Pushable max;

	public Range(kdl.RangeContext ctx, CompilationUnit unit) throws Exception {
		if(ctx.expression().size() > 1)
			min = new Expression(ctx.expression(0), unit);
		else
			min = new Literal<>(DEFAULT_MIN);

		max = new Expression(ctx.expression(ctx.expression().size() - 1), unit);
	}
}
