package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;

public class Condition {

	public static final boolean DEFAULT_POSITIVE = false;

	public final Pushable a;
	public final Pushable b;
	public final Comparator cmp;
	public final boolean positive;

	public Condition(final Pushable a, final Pushable b, final Comparator cmp, final boolean positive) {
		// check that the arguments' existence is valid
		if(a == null)
			throw new NullPointerException();
		if(b == null && cmp != null)
			throw new NullPointerException();
		if(cmp == null && b != null)
			throw new NullPointerException();

		this.a = a;
		this.b = b;
		this.cmp = cmp;
		this.positive = positive;
	}

	/**
	 * Makes a Condition that uses the default positive.
	 *
	 * @param a   arg 1
	 * @param b   arg 2
	 * @param cmp comparison to be made
	 */
	public Condition(final Pushable a, final Pushable b, final Comparator cmp) {
		this(a, b, cmp, DEFAULT_POSITIVE);
	}

	/**
	 * Makes a Condition that checks the usability of a. Uses the default positive.
	 *
	 * @param a any Pushable
	 */
	public Condition(final Pushable a) {
		this(a, null, null);
	}

	/**
	 * Makes a Condition that checks the usability of a.
	 *
	 * @param a        any Pushable
	 * @param positive Whether to make a positive or negative jump
	 */
	public Condition(final Pushable a, final boolean positive) {
		this(a, null, null, positive);
	}

	public static Condition parseCondition(kdl.ConditionContext ctx, Actor actor) throws Exception {
		if(ctx.expression().size() > 1)
			return new Condition(new Expression(actor.unit.type, ctx.expression(0), actor), new Expression(actor.unit.type, ctx.expression(1), actor), Comparator.match(ctx.comparator().getText()));
		else
			return new Condition(new Expression(actor.unit.type, ctx.expression(0), actor));
	}

}
