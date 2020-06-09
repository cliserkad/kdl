package com.xarql.kdl;

public class ArrayAccess {
	public final Variable var;
	public final Value    index;

	public ArrayAccess(final Variable var, final Value index) {
		this.var = var;
		this.index = index;
	}

}
