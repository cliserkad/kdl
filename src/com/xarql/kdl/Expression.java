package com.xarql.kdl;

public class Expression {
	public final Value    partA;
	public final Value    partB;
	public final Operator operator;

	public Expression(Value partA, Value partB, Operator operator) {
		if(!partA.isBaseType() || !partB.isBaseType())
			throw new IllegalArgumentException("Expressions may only contain BaseTypes");
		this.operator = operator;
		this.partA = partA;
		this.partB = partB;
	}

	public Expression(Value partA) {
		this(partA, null, null);
	}

	public boolean isValueOnly() {
		return partA != null && partB == null && operator == null;
	}

	@Override
	public String toString() {
		return "(" + partA.value + ") " + operator + " (" + partB.value + ")";
	}
}
