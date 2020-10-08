package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.MethodHeader.toParamList;
import static com.xarql.kdl.names.InternalName.INT_WRAPPER;

public class ExternalMethodRouter implements CommonText {

	public static final MethodHeader PRINTLN_MTD = new MethodHeader(new InternalName(PrintStream.class), PRINTLN, toParamList(InternalName.STRING), VOID, ACC_PUBLIC);
	public static final MethodHeader PRINT_MTD = new MethodHeader(new InternalName(PrintStream.class), PRINT, toParamList(InternalName.STRING), VOID, ACC_PUBLIC);
	public static final MethodHeader ERROR_MTD = new MethodHeader(new InternalName(PrintStream.class), ERROR, toParamList(InternalName.STRING), VOID, ACC_PUBLIC);
	public static final MethodHeader PARSE_INT_MTD = new MethodHeader(INT_WRAPPER, "parseInt", toParamList(InternalName.STRING), ReturnValue.INT, ACC_PUBLIC + ACC_STATIC);

	public static void writeMethods(final CompilationUnit unit, int line) {
		MethodVisitor visitor;
		final String arg = "input";
		MethodHeader def;

		// add println method to class
		visitor = unit.defineMethod(unit.addMethodDef(PRINTLN_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitFieldInsn(GETSTATIC, new InternalName(System.class).nameString(), "out", new InternalName(PrintStream.class).objectString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invoke(visitor);
		unit.getCurrentScope().end(1, visitor, ReturnValue.VOID);

		// add print method to class
		visitor = unit.defineMethod(unit.addMethodDef(PRINT_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitFieldInsn(GETSTATIC, new InternalName(System.class).nameString(), "out", new InternalName(PrintStream.class).objectString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINT_MTD.invoke(visitor);
		unit.getCurrentScope().end(1, visitor, ReturnValue.VOID);

		// add error method to class
		visitor = unit.defineMethod(unit.addMethodDef(ERROR_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitFieldInsn(GETSTATIC, new InternalName(System.class).nameString(), "err", new InternalName(PrintStream.class).objectString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invoke(visitor);
		unit.getCurrentScope().end(1, visitor, ReturnValue.VOID);

		// add parseInt method to class
		visitor = unit.defineMethod(unit.addMethodDef(PARSE_INT_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PARSE_INT_MTD.invoke(visitor);
		visitor.visitInsn(IRETURN); // return int
		unit.getCurrentScope().end(1, visitor, ReturnValue.INT);
	}

}
