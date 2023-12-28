package com.xarql.kdl.ir;

import com.xarql.kdl.antlr.kdl;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * All possible comparison operators
 */
public enum Comparator {

	EQUAL,
	MORE_THAN,
	LESS_THAN,
	MORE_OR_EQUAL,
	LESS_OR_EQUAL,
	NOT_EQUAL,
	REF_EQUAL,
	REF_NOT_EQUAL;

	/**
	 * Converts a ComparatorContext into a Comparator.
	 * Can fail if the parse tree was generated incorrectly.
	 */
	public static Comparator match(kdl.ComparatorContext ctx) {
		int token = ctx.getChild(TerminalNode.class, 0).getSymbol().getType();

		return switch(token) {
			case kdl.EQUAL -> EQUAL;
			case kdl.MORE_THAN -> MORE_THAN;
			case kdl.LESS_THAN -> LESS_THAN;
			case kdl.MORE_OR_EQUAL -> MORE_OR_EQUAL;
			case kdl.LESS_OR_EQUAL -> LESS_OR_EQUAL;
			case kdl.NOT_EQUAL -> NOT_EQUAL;
			case kdl.REF_EQUAL -> REF_EQUAL;
			case kdl.REF_NOT_EQUAL -> REF_NOT_EQUAL;
			// TODO: better error handling
			default -> throw new IllegalArgumentException("Unexpected value: " + ctx.getText());
		};
	}

}
