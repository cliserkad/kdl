package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.CommonText;

public class ConditionalHandler implements CommonText {

	private static kdl.ConditionContext conditionContextof(kdl.ConditionalContext ctx) throws UnimplementedException {
		if(ctx.branch() != null)
			return ctx.branch().condition();
		else if(ctx.loop() != null)
			return ctx.loop().condition();
		else if(ctx.assertion() != null)
			return ctx.assertion().condition();
		else if(ctx.for_loop() != null)
			return null;
		else
			throw new UnimplementedException("Retrieving a conditional's condition failed");
	}

	public static void handle(kdl.ConditionalContext ctx, final Actor actor) throws Exception {
		final kdl.ConditionContext cnd = conditionContextof(ctx);

		final Conditional conditional;
		if(ctx.for_loop() != null)
			conditional = new ForLoop(ctx.for_loop(), actor);
		else if(ctx.branch() != null)
			conditional = new IfElse(Condition.parseCondition(cnd, actor), actor);
		else if(ctx.assertion() != null)
			conditional = new Assertion(Condition.parseCondition(cnd, actor), actor);
		else if(ctx.loop() != null)
			conditional = new WhileLoop(Condition.parseCondition(cnd, actor), actor);
		else
			throw new UnimplementedException("A type of conditional");
		conditional.checkAll(actor);
		conditional.defineOnTrue(ctx, actor);
		conditional.defineOnFalse(ctx, actor);
		conditional.defineExit(actor);
	}

}
