package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;

public class ForLoop extends Conditional {

	public final Variable iterator;

	/**
	 * Sets up the condition, gives it to super, then extracts fields from super's
	 * condition.
	 */
	public ForLoop(final kdl.For_loopContext forLoop, final Actor actor) throws Exception {
		super(setUpForLoop(forLoop, actor), actor);
		iterator = (Variable) condition.a;
	}

	public static Condition setUpForLoop(final kdl.For_loopContext forLoop, final Actor actor) throws Exception {
		final Range r = new Range(forLoop.range(), actor);
		final Variable increment = actor.scope.newVar(forLoop.IDENTIFIER().getText(), BaseType.INT, true);
		r.min.push(actor);
		increment.assign(BaseType.INT.toTypeDescriptor(), actor);
		return new Condition(increment, r.max, Comparator.LESS_THAN);
	}

	@Override
	public void defineOnTrue(final kdl.ConditionalContext ctx, final Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
		actor.unit.consumeBlock(ctx.for_loop().block(), actor);
		new Expression(iterator, new Expression(new Literal<>(1)), Operator.PLUS).push(actor);
		iterator.assign(BaseType.INT.toTypeDescriptor(), actor);
		actor.visitJumpInsn(GOTO, labelSet.check);
	}

}
