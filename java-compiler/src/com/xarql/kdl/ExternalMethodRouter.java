package com.xarql.kdl;

import com.xarql.kdl.calculable.Variable;
import com.xarql.kdl.names.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.INT_WRAPPER;
import static com.xarql.kdl.names.InternalName.internalName;

public class ExternalMethodRouter implements CommonText {
	public static final JavaMethodDef PRINTLN_MTD = new JavaMethodDef(internalName(PrintStream.class), PRINTLN, list(InternalObjectName.STRING), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PRINT_MTD   = new JavaMethodDef(internalName(PrintStream.class), PRINT, list(InternalObjectName.STRING), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef ERROR_MTD   = new JavaMethodDef(internalName(PrintStream.class), ERROR, list(InternalObjectName.STRING), VOID, ACC_PRIVATE + ACC_STATIC);
	public static final JavaMethodDef PARSE_INT_MTD = new JavaMethodDef(INT_WRAPPER, "parseInt", list(InternalObjectName.STRING), ReturnValue.INT_RETURN, ACC_PRIVATE + ACC_STATIC);

	public static void writeMethods(final CompilationUnit unit, int line) {
		MethodVisitor visitor;
		final String arg = "input";

		// add println method to class
		unit.addMethodDef(PRINTLN_MTD.withOwner(unit.getClazz()));
		visitor = unit.defineMethod(PRINTLN_MTD);
		unit.getCurrentScope().newVariable(arg, new InternalObjectName(String.class));
		Label methodStart = new Label();
		visitor.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invoke(visitor);
		visitor.visitInsn(RETURN);
		Label methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : unit.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();

		// add print method to class
		unit.addMethodDef(PRINT_MTD.withOwner(unit.getClazz()));
		visitor = unit.defineMethod(PRINT_MTD);
		unit.getCurrentScope().newVariable(arg, new InternalObjectName(String.class));
		methodStart = new Label();
		visitor.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "out", new InternalObjectName(PrintStream.class).toString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINT_MTD.invoke(visitor);
		visitor.visitInsn(RETURN);
		 methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : unit.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();

		// add error method to class
		unit.addMethodDef(ERROR_MTD.withOwner(unit.getClazz()));
		visitor = unit.defineMethod(ERROR_MTD);
		unit.getCurrentScope().newVariable(arg, new InternalObjectName(String.class));
		methodStart = new Label();
		visitor.visitFieldInsn(GETSTATIC, internalName(System.class).toString(), "err", new InternalObjectName(PrintStream.class).toString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invoke(visitor);
		visitor.visitInsn(RETURN);
		methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : unit.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();

		// add parseInt method to class
		unit.addMethodDef(PARSE_INT_MTD.withOwner(unit.getClazz()));
		visitor = unit.defineMethod(PARSE_INT_MTD);
		unit.getCurrentScope().newVariable(arg, new InternalObjectName(String.class));
		methodStart = new Label();
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PARSE_INT_MTD.invoke(visitor);
		visitor.visitInsn(IRETURN); // return int
		methodEnd = new Label();
		visitor.visitLabel(methodEnd);
		for(Variable lv : unit.getCurrentScope().getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();
	}
}
