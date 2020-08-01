package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlBaseListener;
import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.Text.nonNull;
import static com.xarql.kdl.names.InternalName.internalName;
import static java.lang.System.exit;

public class SourceListener extends kdlBaseListener implements Opcodes, CommonNames {
	public final ClassCreator owner;

	private final ConditionalHandler cmpHandler;
	private final BestList<String>   constantNames = new BestList<>();

	private String pkgName;

	// pass 1 collects imports, classname, and constant names, methodNames
	// pass 2 assigns values to constants
	// pass 3 defines methods
	private int pass;

	public SourceListener(final ClassCreator owner) {
		this.owner = owner;
		pass = 0;
		cmpHandler = new ConditionalHandler(this);
	}

	public static void standardHandle(Exception e) {
		e.printStackTrace();
		if(e instanceof Exception)
			exit(1);
		else if(e instanceof NullPointerException)
			exit(2);
		else if(e instanceof IllegalArgumentException)
			exit(3);
		else if(e instanceof IllegalStateException)
			exit(4);
		else if(e instanceof TokenNotFoundException)
			exit(5);
		else if(e instanceof IncompatibleTypeException)
			exit(6);
		else if(e instanceof UnimplementedException)
			exit(7);
	}

	private static NameAndType parseTypedVariable(kdlParser.TypedVariableContext ctx) {
		String name = ctx.VARNAME().toString();

		InternalName type;
		if(ctx.type().basetype().BOOLEAN() != null) {
			type = BOOLEAN_IN;
		}
		else if(ctx.type().basetype().INT() != null) {
			type = INT_IN;
		}
		else if(ctx.type().basetype().STRING() != null) {
			type = STRING_IN;
		}
		else
			throw new IllegalArgumentException("Couldn't resolve type of variable");

		return new NameAndType(name, type);
	}

	/**
	 * Converts the top item of the stack in to a string
	 * @param name
	 * @param lmv
	 */
	public static void convertToString(InternalObjectName name, LinedMethodVisitor lmv) {
		JavaMethodDef stringValueOf;
		if(name.isBaseType())
			stringValueOf = new JavaMethodDef(STRING_IN, "valueOf", list(name), new ReturnValue(String.class), ACC_PUBLIC + ACC_STATIC);
		else
			stringValueOf = new JavaMethodDef(STRING_IN, "valueOf", list(new InternalObjectName(Object.class)), new ReturnValue(String.class), ACC_PUBLIC + ACC_STATIC);
		lmv.visitMethodInsn(INVOKESTATIC, stringValueOf.owner(), stringValueOf.methodName, stringValueOf.descriptor(), false);
	}

	private static void store(Resolvable source, Variable target, LinedMethodVisitor lmv) throws Exception {
		if(!source.toInternalName().equals(target.toInternalName()))
			System.err.println(source + INCOMPATIBLE + target);
		else {
			source.push(lmv);
			if(source.toBaseType() == INT || source.toBaseType() == BOOLEAN)
				lmv.visitVarInsn(ISTORE, target.localIndex);
			else
				lmv.visitVarInsn(ASTORE, target.localIndex);
		}
	}

	private static void storeDefault(Variable lv, LinedMethodVisitor lmv) {
		if(lv.type.isBaseType()) {
			switch(lv.type.toBaseType()) {
				case INT:
				case BOOLEAN:
					lmv.visitLdcInsn(DEFAULT_INT);
					lmv.visitVarInsn(ISTORE, lv.localIndex);
					break;
				case STRING:
					lmv.visitLdcInsn(DEFAULT_STRING);
					lmv.visitVarInsn(ASTORE, lv.localIndex);
					break;
			}
		}
		else {
			lmv.visitInsn(ACONST_NULL);
			lmv.visitVarInsn(ASTORE, lv.localIndex);
		}
	}

	private static Operator parseOperator(kdlParser.OperatorContext ctx) {
		return Operator.match(ctx.getText());
	}


	@Override
	public void enterPkg(kdlParser.PkgContext ctx) {
		pkgName = ctx.PKG_NAME().getText();
	}

	public int getPass() {
		return pass;
	}

	public void newPass() {
		pass++;
	}

	@Override
	public void enterSee(kdlParser.SeeContext ctx) {
		if(getPass() == 1)
			owner.addImport(new Import(ctx.QUALIFIED_NAME().toString()));
	}

	@Override
	public void enterClazz(final kdlParser.ClazzContext clazzCtx) {
		if(getPass() == 1) {
			pkgName = nonNull(pkgName);
			owner.setClassName(pkgName, clazzCtx.CLASSNAME().toString());
		}
	}

	@Override
	public void enterConstant(final kdlParser.ConstantContext ctx) {
		// collect names
		if(getPass() == 1) {
			final String name = ctx.CONSTNAME().toString();
			if(!constantNames.contains(name))
				constantNames.add(name);
			else
				throw new IllegalArgumentException("The const named " + name + " was taken by another const with value " + owner.getConstant(name).value);
		}
		else if(getPass() == 2) {
			String name = constantNames.get(0);
			Literal lit = Literal.parseLiteral(ctx.literal());
			owner.addConstant(new Constant(name, lit));
		}
	}

	public void consumeStatementSet(final kdlParser.StatementSetContext ctx, LinedMethodVisitor lmv) {
		if(ctx.statement() != null)
			consumeStatement(ctx.statement(), lmv);
		else
			consumeBlock(ctx.block(), lmv);
	}

	private void consumeStatement(final kdlParser.StatementContext ctx, LinedMethodVisitor lmv) {
		if(ctx.variableDeclaration() != null) {
			consumeVariableDeclaration(ctx.variableDeclaration(), lmv);
		}
		else if(ctx.variableAssignment() != null) {
			consumeVariableAssignment(ctx.variableAssignment(), lmv);
		}
		else if(ctx.methodCall() != null) {
			consumeMethodCall(ctx.methodCall(), lmv);
		}
		else if(ctx.conditional() != null) {
			// forward to the handler to partition code
			cmpHandler.handle(ctx.conditional(), lmv);
		}
		else
			standardHandle(new UnimplementedException("A type of statement couldn't be interpreted " + ctx.getText()));
	}

	private void consumeBlock(final kdlParser.BlockContext ctx, LinedMethodVisitor lmv) {
		for(kdlParser.StatementContext statement : ctx.statement())
			consumeStatement(statement, lmv);
	}

	private void dumpShortcuts(final kdlParser.ClazzContext ctx) {
		JavaMethodDef print = new JavaMethodDef(new InternalName(owner), PRINT, list(STRING_ION), VOID, ACC_PRIVATE);


	}

	@Override
	public void enterRun(final kdlParser.RunContext ctx) {
		if(getPass() == 2) {
			owner.addMethodDef(JavaMethodDef.MAIN.withOwner(owner));
		}
		else if(getPass() == 3) {
			final LinedMethodVisitor lmv = owner.defineMethod(JavaMethodDef.MAIN, ctx.start.getLine() + 1);
			new Variable(owner.currentScope, "args", new InternalObjectName(String.class, 1));
			Label methodStart = new Label();
			consumeBlock(ctx.block(), lmv);

			final Label ret = new Label();
			lmv.visitLabel(ret);
			lmv.visitLineNumber(ctx.start.getLine(), ret);
			lmv.visitInsn(Opcodes.RETURN);

			final Label methodEnd = new Label();
			lmv.visitLabel(methodEnd);
			for(Variable lv : owner.currentScope.getVariables())
				lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
			lmv.visitMaxs(0, 0);
			lmv.visitEnd();

			ExternalMethodRouter.writeMethods(owner);
		}
	}

}
