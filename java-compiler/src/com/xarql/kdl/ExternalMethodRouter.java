package com.xarql.kdl;

import com.xarql.kdl.calculable.Variable;
import com.xarql.kdl.names.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;

public class ExternalMethodRouter implements CommonNames {
	public static final JavaMethodDef PRINTLN_MTD = new JavaMethodDef(internalName(PrintStream.class), PRINTLN, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PRINT_MTD   = new JavaMethodDef(internalName(PrintStream.class), PRINT, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef ERROR_MTD   = new JavaMethodDef(internalName(PrintStream.class), ERROR, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PARSE_INT_MTD = new JavaMethodDef(INT_WRAPPER, "parseInt", list(STRING_ION), ReturnValue.INT_RETURN, ACC_PRIVATE + ACC_STATIC);

	public static void writeMethods(final CompilationUnit cu, int line) {
		MethodVisitor visitor;
		final String arg = "input";

		// add println method to class
		cu.addMethodDef(PRINTLN_MTD.withOwner(cu.getClazz()));
		visitor = cu.defineMethod(PRINTLN_MTD);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		Label methodStart = new Label();
		visitor.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		visitor.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invokeVirtual(visitor);
		visitor.visitInsn(RETURN);
		Label methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();

		// add print method to class
		cu.addMethodDef(PRINT_MTD.withOwner(cu.getClazz()));
		visitor = cu.defineMethod(PRINT_MTD);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		 methodStart = new Label();
		visitor.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		visitor.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		PRINT_MTD.invokeVirtual(visitor);
		visitor.visitInsn(RETURN);
		 methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();

		// add error method to class
		cu.addMethodDef(ERROR_MTD.withOwner(cu.getClazz()));
		visitor = cu.defineMethod(ERROR_MTD);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		methodStart = new Label();
		visitor.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "err", new InternalObjectName(PrintStream.class).toString());
		visitor.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invokeVirtual(visitor);
		visitor.visitInsn(RETURN);
		methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();

		// add parseInt method to class
		cu.addMethodDef(PARSE_INT_MTD.withOwner(cu.getClazz()));
		visitor = cu.defineMethod(PARSE_INT_MTD);
		new Variable(cu.getCurrentScope(), arg, new InternalObjectName(String.class));
		methodStart = new Label();
		visitor.visitVarInsn(ALOAD, cu.getLocalVariable(arg).localIndex); // load input
		PARSE_INT_MTD.invokeStatic(visitor);
		visitor.visitInsn(IRETURN); // return int
		methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : cu.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();
	}
}
