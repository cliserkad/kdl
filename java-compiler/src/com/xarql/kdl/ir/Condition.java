package com.xarql.kdl.ir;

public class Condition {
	public final Resolvable a;
	public final Resolvable b;
	public final Comparator cmp;

	public Condition(Resolvable a, Resolvable b, Comparator cmp) {
		this.a = a;
		this.b = b;
		this.cmp = cmp;
	}
}
