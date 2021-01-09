package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.TypeDescriptor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Actor extends MethodVisitor implements Opcodes, CommonText {

	public final CompilationUnit unit;
	public final Scope scope;

	private Actor(final MethodVisitor methodVisitor, final CompilationUnit unit, final String name) {
		super(Opcodes.ASM8, methodVisitor);
		this.unit = unit;
		this.scope = new Scope("Method " + name + " of unit " + unit, methodVisitor);
	}

	public static Actor build(final MethodHeader methodHeader, final CompilationUnit unit) {
		final MethodVisitor methodVisitor = unit.cw.visitMethod(methodHeader.access, methodHeader.name, methodHeader.descriptor(), null, null);
		return new Actor(methodVisitor, unit, methodHeader.name);
	}

	public void writeReturn(TypeDescriptor yield) {
		if(TypeDescriptor.VOID.equals(yield))
			visitInsn(RETURN);
		else if(yield.isBaseType() && !yield.isArray()) {
			switch(yield.toBaseType()) {
				case BOOLEAN:
				case BYTE:
				case CHAR:
				case SHORT:
				case INT:
					visitInsn(IRETURN);
					break;
				case FLOAT:
					visitInsn(FRETURN);
					break;
				case LONG:
					visitInsn(LRETURN);
					break;
				case DOUBLE:
					visitInsn(DRETURN);
					break;
				case STRING:
					visitInsn(ARETURN);
					break;
			}
		} else
			visitInsn(ARETURN);
	}
}
