package com.xarql.kdl.ir;

public class Condition {
	public final Pushable   a;
	public final Pushable   b;
	public final Comparator cmp;

	public Condition(Pushable a, Pushable b, Comparator cmp) {
		this.a = a;
		this.b = b;
		this.cmp = cmp;
	}
}
