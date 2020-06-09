package com.xarql.kdl;

public class ArrayAccess {
	public final Variable   var;
	public final Expression index;

	public ArrayAccess(final Variable var, final Expression index) {
		this.var = var;
		this.index = index;
	}

}
