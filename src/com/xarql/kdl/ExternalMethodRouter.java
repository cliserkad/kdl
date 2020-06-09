package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalObjectName;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;

public class ExternalMethodRouter implements Opcodes, CommonNames {
	public static final MethodDef PRINTLN_MTD = new MethodDef(MethodDef.Type.MTD, PRINTLN, list(STRING_ION), VOID, ACC_PUBLIC + ACC_STATIC);
	public static final MethodDef PRINT_MTD   = new MethodDef(MethodDef.Type.MTD, PRINT, list(STRING_ION), VOID, ACC_PUBLIC + ACC_STATIC);

	public static boolean isMethodExternal(String name) {
		return resolveMethod(name) != null;
	}

	public static MethodDef resolveMethod(String name) {
		switch(name) {
			case PRINT:
				return PRINT_MTD;
			case PRINTLN:
				return PRINTLN_MTD;
			default:
				return null;
		}
	}

	public static Label writeMethod(String methodName, LinedMethodVisitor lmv, Object... parameters) {
		if(methodName.equals(PRINTLN)) {
			final Label print = new Label();
			lmv.visitLabel(print);
			lmv.visitLineNumber(lmv.getLine(), print);
			lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
			lmv.visitInsn(SWAP);
			lmv.visitMethodInsn(INVOKEVIRTUAL, internalName(PrintStream.class).toString(), PRINTLN, PRINTLN_MTD.descriptor(), false);
			return print;
		}
		else if(methodName.equals(PRINT)) {
			final Label print = new Label();
			lmv.visitLabel(print);
			lmv.visitLineNumber(lmv.getLine(), print);
			lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
			lmv.visitInsn(SWAP);
			lmv.visitMethodInsn(INVOKEVIRTUAL, internalName(PrintStream.class).toString(), PRINT, PRINT_MTD.descriptor(), false);
			return print;
		}
		else {
			if(isMethodExternal(methodName))
				SourceListener.standardHandle(new UnimplementedException("ExternalMethodRouter method"));
			else
				SourceListener.standardHandle(new IllegalArgumentException("The method " + methodName + " is not an external method"));
		}
		lmv.incrementLine();
		return new Label();
	}
}
