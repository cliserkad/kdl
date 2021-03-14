package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.TypeDescriptor;

import java.io.PrintStream;

import static com.xarql.kdl.MethodHeader.toParamList;
import static com.xarql.kdl.names.TypeDescriptor.INT_WRAPPER;

public class ExternalMethodRouter implements CommonText {

	public static final MethodHeader PRINTLN_MTD = new MethodHeader(new TypeDescriptor(PrintStream.class), PRINTLN, toParamList(BaseType.STRING), TypeDescriptor.VOID, ACC_PUBLIC);
	public static final MethodHeader PRINT_MTD = new MethodHeader(new TypeDescriptor(PrintStream.class), PRINT, toParamList(BaseType.STRING), TypeDescriptor.VOID, ACC_PUBLIC);
	public static final MethodHeader ERROR_MTD = new MethodHeader(new TypeDescriptor(PrintStream.class), ERROR, toParamList(BaseType.STRING), TypeDescriptor.VOID, ACC_PUBLIC);
	public static final MethodHeader PARSE_INT_MTD = new MethodHeader(INT_WRAPPER, "parseInt", toParamList(BaseType.STRING), BaseType.INT.toTypeDescriptor(),
			ACC_PUBLIC + ACC_STATIC);

	public static void writeMethods(final CompilationUnit unit, int line) throws Exception {
		Actor actor;
		final String arg = "testing";
		MethodHeader def;

		// add println method to class
		actor = unit.defineMethod(unit.registerMethod(PRINTLN_MTD.withOwner(unit.getType()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		actor.scope.newVar(arg, new TypeDescriptor(String.class));
		actor.visitFieldInsn(GETSTATIC, new TypeDescriptor(System.class).qualifiedName(), "out", new TypeDescriptor(PrintStream.class).arrayName());
		actor.visitVarInsn(ALOAD, actor.scope.get(arg).localIndex); // load input
		PRINTLN_MTD.push(actor);
		actor.scope.end(line, actor, TypeDescriptor.VOID);

		// add print method to class
		actor = unit.defineMethod(unit.registerMethod(PRINT_MTD.withOwner(unit.getType()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		actor.scope.newVar(arg, new TypeDescriptor(String.class));
		actor.visitFieldInsn(GETSTATIC, new TypeDescriptor(System.class).qualifiedName(), "out", new TypeDescriptor(PrintStream.class).arrayName());
		actor.visitVarInsn(ALOAD, actor.scope.get(arg).localIndex); // load input
		PRINT_MTD.push(actor);
		actor.scope.end(line, actor, TypeDescriptor.VOID);

		// add error method to class
		actor = unit.defineMethod(unit.registerMethod(ERROR_MTD.withOwner(unit.getType()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		actor.scope.newVar(arg, new TypeDescriptor(String.class));
		actor.visitFieldInsn(GETSTATIC, new TypeDescriptor(System.class).qualifiedName(), "err", new TypeDescriptor(PrintStream.class).arrayName());
		actor.visitVarInsn(ALOAD, actor.scope.get(arg).localIndex); // load input
		PRINTLN_MTD.push(actor);
		actor.scope.end(line, actor, TypeDescriptor.VOID);

		// add parseInt method to class
		actor = unit.defineMethod(unit.registerMethod(PARSE_INT_MTD.withOwner(unit.getType()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		actor.scope.newVar(arg, new TypeDescriptor(String.class));
		actor.visitVarInsn(ALOAD, actor.scope.get(arg).localIndex); // load input
		PARSE_INT_MTD.push(actor);
		actor.visitInsn(IRETURN); // return int
		actor.scope.end(line, actor, BaseType.INT.toTypeDescriptor());
	}

}
