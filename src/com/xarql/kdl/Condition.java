package com.xarql.kdl;

public class Condition {
	public final Value      xprA;
	public final Value      xprB;
	public final Comparator cmp;

	public Condition(Value xpr1, Value xpr2, Comparator cmp) {
		this.xprA = xpr1;
		this.xprB = xpr2;
		this.cmp = cmp;
	}
}
