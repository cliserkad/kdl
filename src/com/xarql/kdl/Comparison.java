package com.xarql.kdl;

public class Comparison {
	public final Expression xprA;
	public final Expression xprB;
	public final Comparator cmp;

	public Comparison(Expression xpr1, Expression xpr2, Comparator cmp) {
		this.xprA = xpr1;
		this.xprB = xpr2;
		this.cmp = cmp;
	}
}
