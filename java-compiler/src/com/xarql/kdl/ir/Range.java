package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;

public class Range {

	public static final int DEFAULT_MIN = 0;

	public final Pushable min;
	public final Pushable max;

	public Range(final kdl.RangeContext ctx, final Actor actor) throws Exception {
		if(ctx.expression().size() > 1)
			min = new Expression(actor.unit.type, ctx.expression(0), actor);
		else
			min = new Literal<>(DEFAULT_MIN);

		max = new Expression(actor.unit.type, ctx.expression(ctx.expression().size() - 1), actor);
	}

}
