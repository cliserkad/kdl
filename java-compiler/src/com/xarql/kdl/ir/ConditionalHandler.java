package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.*;

public class ConditionalHandler implements CommonText {

	private final CompilationUnit owner;

	public ConditionalHandler(CompilationUnit owner) {
		this.owner = owner;
	}

	private kdl.ConditionContext conditionContextof(kdl.ConditionalContext ctx) throws UnimplementedException {
		if(ctx.r_if() != null)
			return ctx.r_if().condition();
		else if(ctx.r_while() != null)
			return ctx.r_while().condition();
		else if(ctx.assertion() != null)
			return ctx.assertion().condition();
		else if(ctx.for_loop() != null)
			return null;
		else
			throw new UnimplementedException("Retrieving a conditional's condition failed");
	}

	public void handle(kdl.ConditionalContext ctx, final Actor actor) throws Exception {
		final kdl.ConditionContext cnd = conditionContextof(ctx);

		final Conditional conditional;
		if(ctx.for_loop() != null)
			conditional = new ForLoop(ctx.for_loop(), actor);
		else if(ctx.r_if() != null)
			conditional = new IfElse(Condition.parseCondition(cnd, actor), actor);
		else if(ctx.assertion() != null)
			conditional = new Assertion(Condition.parseCondition(cnd, actor), actor);
		else if(ctx.r_while() != null)
			conditional = new WhileLoop(Condition.parseCondition(cnd, actor), actor);
		else
			throw new UnimplementedException("A type of conditional");
		conditional.checkAll(actor);
		conditional.defineOnTrue(ctx, actor);
		conditional.defineOnFalse(ctx, actor);
		conditional.defineExit(actor);
	}

}
