package com.xarql.kdl;

public enum Comparator {
	EQUAL("="), MORE_THAN(">"), LESS_THAN("<"), MORE_OR_EQUAL(">="), LESS_OR_EQUAL("<="), NOT_EQUAL("!="), REF_EQUAL("?"), REF_NOT_EQUAL("!?");

	String rep;

	Comparator(String rep) {
		this.rep = rep;
	}

	public static boolean isComparator(String rep) {
		return match(rep) != null;
	}

	public static Comparator match(String rep) {
		for(Comparator c : values())
			if(rep.equals(c.rep))
				return c;
		return null;
	}
}
