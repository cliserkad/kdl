package com.xarql.kdl;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Actor extends MethodVisitor {

	public final CompilationUnit unit;

	public Actor(final MethodVisitor methodVisitor, final CompilationUnit unit) {
		super(Opcodes.ASM8, methodVisitor);
		this.unit = unit;
	}

}
