package com.xarql.kdl;

import com.xarql.kdl.calculable.Variable;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalObjectName;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;

public class ExternalMethodRouter implements CommonNames {
	public static final JavaMethodDef PRINTLN_MTD = new JavaMethodDef(internalName(PrintStream.class), PRINTLN, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PRINT_MTD   = new JavaMethodDef(internalName(PrintStream.class), PRINT, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef ERROR_MTD   = new JavaMethodDef(internalName(PrintStream.class), ERROR, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PARSE_INT_MTD = new JavaMethodDef(INT_WRAPPER, "parseInt", list(STRING_ION), INT_RV, ACC_PRIVATE + ACC_STATIC);

	public static void writeMethods(final CompilationUnit cu) {
		LinedMethodVisitor lmv;
		final String arg = "input";

		// add println method to class
		cu.addMethodDef(PRINTLN_MTD.withOwner(cu.getClazz()));
		lmv = cu.defineMethod(PRINTLN_MTD, 1);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		Label methodStart = new Label();
		lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		lmv.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invokeVirtual(lmv);
		lmv.visitInsn(RETURN);
		Label methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();

		// add print method to class
		cu.addMethodDef(PRINT_MTD.withOwner(cu.getClazz()));
		lmv = cu.defineMethod(PRINT_MTD, 1);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		 methodStart = new Label();
		lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		lmv.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		PRINT_MTD.invokeVirtual(lmv);
		lmv.visitInsn(RETURN);
		 methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();

		// add error method to class
		cu.addMethodDef(ERROR_MTD.withOwner(cu.getClazz()));
		lmv = cu.defineMethod(ERROR_MTD, 1);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		methodStart = new Label();
		lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "err", new InternalObjectName(PrintStream.class).toString());
		lmv.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		ERROR_MTD.invokeVirtual(lmv);
		lmv.visitInsn(RETURN);
		methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();

		// add parseInt method to class
		cu.addMethodDef(PARSE_INT_MTD.withOwner(cu.getClazz()));
		lmv = cu.defineMethod(PARSE_INT_MTD, 1);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		methodStart = new Label();
		lmv.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		PARSE_INT_MTD.invokeStatic(lmv);
		lmv.visitInsn(IRETURN); // return int
		methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();
	}
}
