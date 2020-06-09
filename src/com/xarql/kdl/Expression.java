package com.xarql.kdl;

import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;

public class Expression {
	public final InternalObjectName partA;
	public final InternalObjectName partB;
	public final Operator           operator;

	public Expression(InternalObjectName partA, InternalObjectName partB, Operator operator) {
		if(!partA.isBaseType() || !partB.isBaseType())
			throw new IllegalArgumentException("Expressions may only contain BaseTypes");
		this.operator = operator;
		this.partA = partA;
		this.partB = partB;
	}

	public Expression(InternalName in1, InternalName in2, Operator operator) {
		this(in1.object(), in2.object(), operator);
	}
}
