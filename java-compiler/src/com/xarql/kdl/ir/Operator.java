package com.xarql.kdl.ir;

public enum Operator {

	PLUS('+'), MINUS('-'), MULTIPLY('*'), DIVIDE('/'), MODULUS('%');

	char rep;

	Operator(char rep) {
		this.rep = rep;
	}

	public static Operator match(char rep) {
		for (Operator op : Operator.values())
			if (op.rep == rep)
				return op;
		return null;
	}

	public static Operator match(String s) {
		if (s != null && !s.isEmpty()) {
			return match(s.charAt(0));
		} else
			return null;
	}

}
