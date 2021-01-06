package com.xarql.kdl.ir;

import com.xarql.kdl.Text;

public enum Operator {

	PLUS("+"),
	MINUS("-"),
	MULTIPLY("*"),
	DIVIDE("/"),
	MODULUS("%"),
	RESOLVE("."),
	INDEX_ACCESS("["),
	NOT("!"),
	INCREMENT("++"),
	DECREMENT("--"),
	BIT_SHIFT_LEFT("<<"),
	BIT_SHIFT_RIGHT(">>"),
	BIT_SHIFT_RIGHT_UNSIGNED(">>>"),
	BIT_OR("|"),
	BIT_AND("&"),
	BIT_XOR("^"),
	OR("||"),
	AND("&&"),
	XOR("^^");

	public final String rep;

	Operator(String rep) {
		this.rep = rep;
	}

	public static Operator match(String s) {
		if(Text.isEmpty(s))
			return null;
		else
			return match(s.trim());
	}

}
