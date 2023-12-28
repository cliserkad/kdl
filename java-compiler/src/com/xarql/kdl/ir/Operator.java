package com.xarql.kdl.ir;

import com.xarql.kdl.antlr.kdl;
import org.antlr.v4.runtime.tree.TerminalNode;

public enum Operator {

	ADD,
	SUB,
	MUL,
	DIV,
	MOD;

	public static Operator match(kdl.OperatorContext ctx) {
		// TODO make a better method for extracting the token id / terminal node type
		int token = ctx.getChild(TerminalNode.class, 0).getSymbol().getType();

		return switch(token) {
			case kdl.ADD -> ADD;
			case kdl.SUB -> SUB;
			case kdl.MUL -> MUL;
			case kdl.DIV -> DIV;
			case kdl.MOD -> MOD;
			// TODO: better error handling
			default -> throw new IllegalArgumentException("Unexpected value: " + ctx.getText());
		};
	}

}
