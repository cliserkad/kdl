package com.xarql.kdl;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LinedMethodVisitor extends MethodVisitor implements Opcodes {
	private int line;

	public LinedMethodVisitor(MethodVisitor mv, int line) {
		super(ASM8, mv);
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public void incrementLine() {
		setLine(getLine() + 1);
	}
}
