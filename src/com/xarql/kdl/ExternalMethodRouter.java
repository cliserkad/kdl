package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.NameFormats.internalName;
import static com.xarql.kdl.names.NameFormats.internalObjectName;

public class ExternalMethodRouter implements Opcodes, CommonNames {
	public static final MethodDef PRINTLN_MTD = new MethodDef(MethodDef.Type.MTD, PRINTLN, list(STRING_ION), VOID_RETURN, ACC_PUBLIC + ACC_STATIC);

	public static Label writeMethod(String methodName, LinedMethodVisitor lmv, Object... parameters) {
		if(methodName.equals(PRINTLN)) {
			final Label print = new Label();
			lmv.visitLabel(print);
			lmv.visitLineNumber(lmv.getLine(), print);
			lmv.visitFieldInsn(GETSTATIC, internalName(System.class), "out", internalObjectName(PrintStream.class));
			lmv.visitInsn(SWAP);
			lmv.visitMethodInsn(INVOKEVIRTUAL, internalName(PrintStream.class), PRINTLN, PRINTLN_MTD.descriptor(), false);
			return print;
		}
		lmv.incrementLine();
		return new Label();
	}
}
