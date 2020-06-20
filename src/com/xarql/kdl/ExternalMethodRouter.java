package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalObjectName;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;

public class ExternalMethodRouter implements Opcodes, CommonNames {
	public static final JavaMethodDef PRINTLN_MTD = new JavaMethodDef(internalName(PrintStream.class), PRINTLN, list(STRING_ION), VOID, ACC_PUBLIC + ACC_STATIC);
	public static final JavaMethodDef PRINT_MTD   = new JavaMethodDef(internalName(PrintStream.class), PRINT, list(STRING_ION), VOID, ACC_PUBLIC + ACC_STATIC);
	public static final JavaMethodDef ERROR_MTD   = new JavaMethodDef(internalName(PrintStream.class), PRINT, list(STRING_ION), VOID, ACC_PUBLIC + ACC_STATIC);

	public static boolean isMethodExternal(String name) {
		return resolveMethod(name) != null;
	}

	public static JavaMethodDef resolveMethod(String name) {
		switch(name) {
			case PRINT:
				return PRINT_MTD;
			case PRINTLN:
				return PRINTLN_MTD;
			case ERROR:
				return ERROR_MTD;
			default:
				return null;
		}
	}

	public static Label writeMethod(String methodName, LinedMethodVisitor lmv, Object... params) {
		if(methodName.equals(PRINTLN)) {
			final Label print = new Label();
			lmv.visitLabel(print);
			lmv.visitLineNumber(lmv.getLine(), print);
			lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
			lmv.visitInsn(SWAP);
			PRINTLN_MTD.invokeVirtual(lmv);
			return print;
		}
		else if(methodName.equals(PRINT)) {
			final Label print = new Label();
			lmv.visitLabel(print);
			lmv.visitLineNumber(lmv.getLine(), print);
			lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
			lmv.visitInsn(SWAP);
			PRINT_MTD.invokeVirtual(lmv);
			return print;
		}
		else if(methodName.equals(ERROR)) {
			final Label error = new Label();
			lmv.visitLabel(error);
			lmv.visitLineNumber(lmv.getLine(), error);
			lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "err", new InternalObjectName(PrintStream.class).toString());
			lmv.visitInsn(SWAP);
			ERROR_MTD.invokeVirtual(lmv);
			return error;
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
