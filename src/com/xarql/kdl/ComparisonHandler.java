package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.Opcodes;

public class ComparisonHandler implements CommonNames, Opcodes {
	private final SourceListener owner;

	public ComparisonHandler(SourceListener owner) {
		this.owner = owner;
	}

	public static void handle(Comparator cmp, LinedMethodVisitor lmv) {

	}
}
