package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalObjectName;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;

public class ExternalMethodRouter implements Opcodes, CommonNames {
	public static final JavaMethodDef PRINTLN_MTD = new JavaMethodDef(internalName(PrintStream.class), PRINTLN, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PRINT_MTD   = new JavaMethodDef(internalName(PrintStream.class), PRINT, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef ERROR_MTD   = new JavaMethodDef(internalName(PrintStream.class), PRINT, list(STRING_ION), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PARSE_INT_MTD = new JavaMethodDef(internalName(Integer.class), "parseInt", list(STRING_ION), INT_RV, ACC_PRIVATE + ACC_STATIC);

	public static void writeMethods(final ClassCreator owner) {
		LinedMethodVisitor lmv;
		final String arg = "input";

		// add println method to class
		owner.addMethodDef(PRINTLN_MTD.withOwner(owner));
		lmv = owner.defineMethod(PRINTLN_MTD, 1);
		new Variable(owner.currentScope, arg, new InternalObjectName(String.class));
		Label methodStart = new Label();
		lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		lmv.visitVarInsn(ALOAD, owner.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invokeVirtual(lmv);
		lmv.visitInsn(RETURN);
		Label methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : owner.currentScope.getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();

		// add print method to class
		owner.addMethodDef(PRINT_MTD.withOwner(owner));
		lmv = owner.defineMethod(PRINT_MTD, 1);
		new Variable(owner.currentScope, arg, new InternalObjectName(String.class));
		 methodStart = new Label();
		lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		lmv.visitVarInsn(ALOAD, owner.getLocalVariable(arg).localIndex); // load input
		PRINT_MTD.invokeVirtual(lmv);
		lmv.visitInsn(RETURN);
		 methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : owner.currentScope.getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();

		// add error method to class
		owner.addMethodDef(ERROR_MTD.withOwner(owner));
		lmv = owner.defineMethod(ERROR_MTD, 1);
		new Variable(owner.currentScope, arg, new InternalObjectName(String.class));
		methodStart = new Label();
		lmv.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "err", new InternalObjectName(PrintStream.class).toString());
		lmv.visitVarInsn(ALOAD, owner.getLocalVariable(arg).localIndex); // load input
		ERROR_MTD.invokeVirtual(lmv);
		lmv.visitInsn(RETURN);
		methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : owner.currentScope.getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();

		// add parseInt method to class
		owner.addMethodDef(PARSE_INT_MTD.withOwner(owner));
		lmv = owner.defineMethod(PARSE_INT_MTD, 1);
		new Variable(owner.currentScope, arg, new InternalObjectName(String.class));
		methodStart = new Label();
		lmv.visitVarInsn(ALOAD, owner.getLocalVariable(arg).localIndex); // load input
		PARSE_INT_MTD.invokeStatic(lmv);
		lmv.visitInsn(IRETURN); // return int
		methodEnd = new Label();
		lmv.visitLabel(methodEnd);
		for(Variable lv : owner.currentScope.getVariables())
			lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		lmv.visitMaxs(0, 0);
		lmv.visitEnd();
	}
}
