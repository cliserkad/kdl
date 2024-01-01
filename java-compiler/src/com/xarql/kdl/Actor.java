package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Actor extends MethodVisitor implements Opcodes, CommonText {

	public final CompilationUnit unit;

	public Actor(final MethodVisitor methodVisitor, final CompilationUnit unit) {
		super(Opcodes.ASM8, methodVisitor);
		this.unit = unit;
	}

	public void writeReturn(ReturnValue rv) {
		if(rv == null)
			visitInsn(RETURN);
		else if(rv.isBaseType() && !rv.toInternalName().isArray()) {
			switch(rv.toBaseType()) {
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
