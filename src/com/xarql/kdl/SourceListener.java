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

	private final ExpressionHandler xprHandler;

	private String pkgName;

	// pass 0 does nothing
	// pass 1 collects imports, classname, and constants, methodNames
	// pass 2 defines methods
	private int pass;

	public SourceListener(final ClassCreator owner) {
		this.owner = owner;
		pass = 0;
		xprHandler = new ExpressionHandler(this);
	}

	public static void standardHandle(Exception e) {
		System.err.println(e.getMessage());
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

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}

	public static Literal<?> pushLiteral(kdlParser.LiteralContext literal, LinedMethodVisitor lmv) {
		try {
			if(literal.bool() != null) {
				final boolean out = parseBool(literal);
				lmv.visitLdcInsn(out);
				return new Literal<>(out);
			}
			else if(literal.number() != null) {
				final int out = parseNumber(literal);
				lmv.visitLdcInsn(out);
				return new Literal<>(out);
			}
			else if(literal.STRING_LIT() != null) {
				final String out = parseStringLit(literal);
				lmv.visitLdcInsn(out);
				return new Literal<>(out);
			}
			else
				throw new UnimplementedException(BASETYPE_MISS);
		} catch(UnimplementedException ue) {
			standardHandle(ue);
			return null;
		}
	}

	public static LocalVariable pushVariable(LocalVariable lv, MethodVisitor mv) {
		try {
			if(lv.type.isBaseType() && lv.type.toBaseType() != STRING) {
				if(lv.type.toBaseType() == INT) {
					mv.visitVarInsn(ILOAD, lv.localIndex);
				}
				else if(lv.type.toBaseType() == BOOLEAN) {
					mv.visitVarInsn(ILOAD, lv.localIndex);
				}
				else
					throw new UnimplementedException(SWITCH_BASETYPE);
			}
			else {
				mv.visitVarInsn(ALOAD, lv.localIndex);
			}
			return lv;
		} catch(UnimplementedException ue) {
			standardHandle(ue);
		}
		return lv;
	}

	private static String parseStringLit(kdlParser.LiteralContext literal) {
		try {
			if(literal.STRING_LIT() != null) {
				return crush(literal.STRING_LIT().toString());
			}
			else
				throw new IncompatibleTypeException("Couldn't parse string from literal " + literal.getText() + " because none was present");
		} catch(IncompatibleTypeException ite) {
			standardHandle(ite);
			return DEFAULT_STRING;
		}
	}

	private static boolean parseBool(kdlParser.LiteralContext literal) {
		try {
			if(literal.bool() != null)
				return parseBool(literal.bool());
			else
				throw new IncompatibleTypeException("Couldn't parse boolean from literal " + literal.getText() + " because none was present");
		} catch(IncompatibleTypeException ite) {
			standardHandle(ite);
			return DEFAULT_BOOLEAN;
		}
	}

	private static boolean parseBool(kdlParser.BoolContext bool) {
		if(bool.TRUE() != null)
			return true;
		else
			return false;
	}

	private static int parseNumber(kdlParser.LiteralContext literal) {
		try {
			if(literal.number() != null)
				return parseNumber(literal.number());
			else
				throw new IncompatibleTypeException("Couldn't parse number from literal " + literal.getText() + " because none was present");
		} catch(IncompatibleTypeException ite) {
			standardHandle(ite);
			return DEFAULT_INT;
		}
	}

	private static int parseNumber(kdlParser.NumberContext number) {
		try {
			return Integer.valueOf(number.getText());
		} catch(NullPointerException npe) {
			TokenNotFoundException notFound = new TokenNotFoundException("The expected NumberContext wasn't provided");
			standardHandle(notFound);
			return 0;
		} catch(final NumberFormatException nfe) {
			return DEFAULT_INT;
		}
	}

	private static BaseType parseLiteralType(kdlParser.LiteralContext lit) {
		if(lit.bool() != null)
			return BaseType.BOOLEAN;
		else if(lit.number() != null) {
			return BaseType.INT;
		}
		else if(lit.STRING_LIT() != null) {
			return BaseType.STRING;
		}
		else
			throw new IllegalArgumentException("Couldn't parse type of literal " + lit.getText());
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

	private static void convertToString(InternalObjectName name, LinedMethodVisitor lmv) {
		MethodDef stringValueOf;
		if(name.isBaseType())
			stringValueOf = new MethodDef(MethodDef.Type.MTD, "valueOf", list(name), new ReturnValue(String.class), ACC_PUBLIC + ACC_STATIC);
		else
			stringValueOf = new MethodDef(MethodDef.Type.MTD, "valueOf", list(internalName(Object.class).object()), new ReturnValue(String.class), ACC_PUBLIC + ACC_STATIC);
		lmv.visitMethodInsn(INVOKESTATIC, STRING_IN_S, stringValueOf.methodName, stringValueOf.descriptor(), false);
	}

	/**
	 * Stores any literal at the given localIndex
	 * @param lit
	 * @param localIndex
	 * @param lmv
	 * @throws TokenNotFoundException
	 * @throws UnimplementedException
	 */
	private static void storeLiteral(kdlParser.LiteralContext lit, int localIndex, LinedMethodVisitor lmv) {
		if(lit.bool() != null) {
			lmv.visitLdcInsn(parseBool(lit.bool()));
			lmv.visitVarInsn(ISTORE, localIndex);
		}
		else if(lit.number() != null) {
			lmv.visitLdcInsn(parseNumber(lit.number()));
			lmv.visitVarInsn(ISTORE, localIndex);
		}
		else if(lit.STRING_LIT() != null) {
			lmv.visitLdcInsn(parseStringLit(lit));
			lmv.visitVarInsn(ASTORE, localIndex);
		}
		else
			standardHandle(new UnimplementedException(BASETYPE_MISS));
	}

	private static void storeVariable(LocalVariable source, LocalVariable target, LinedMethodVisitor lmv) {
		if(!source.type.equals(target.type))
			standardHandle(new IncompatibleTypeException("Type " + source.type + " is incompatible with type " + target.type));
		if(source.type.isBaseType()) {
			switch(source.type.toBaseType()) {
				case INT:
				case BOOLEAN:
					lmv.visitVarInsn(ILOAD, source.localIndex);
					lmv.visitVarInsn(ISTORE, target.localIndex);
					break;
				case STRING:
					lmv.visitVarInsn(ALOAD, source.localIndex);
					lmv.visitVarInsn(ASTORE, target.localIndex);
					break;
				default:
					standardHandle(new UnimplementedException(SWITCH_BASETYPE));
			}
		}
		else {
			lmv.visitVarInsn(ALOAD, source.localIndex);
			lmv.visitVarInsn(ASTORE, target.localIndex);
		}
	}

	private static void loadVariable(LocalVariable lv, LinedMethodVisitor lmv) {
		try {
			if(lv.type.isBaseType()) {
				switch(lv.type.toBaseType()) {
					case INT:
					case BOOLEAN:
						lmv.visitVarInsn(ILOAD, lv.localIndex);
						break;
					case STRING:
						lmv.visitVarInsn(ALOAD, lv.localIndex);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			}
			else
				lmv.visitVarInsn(ALOAD, lv.localIndex);
		} catch(UnimplementedException ue) {
			standardHandle(ue);
		}
	}

	private static void storeDefault(LocalVariable lv, LinedMethodVisitor lmv) {
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

	public static Constant<?> pushConstant(Constant<?> c, LinedMethodVisitor lmv) {
		if(c.isBaseType()) {
			lmv.visitLdcInsn(c.value);
			return c;
		}
		else {
			standardHandle(new UnimplementedException("Constant is using a reference type"));
			return null;
		}
	}

	private static void storeConstant(Constant<?> c, int localIndex, LinedMethodVisitor lmv) {
		pushConstant(c, lmv);
		if(c.isBaseType()) {
			switch(c.toBaseType()) {
				case INT:
				case BOOLEAN:
					lmv.visitVarInsn(ISTORE, localIndex);
					break;
				case STRING:
					lmv.visitVarInsn(ASTORE, localIndex);
					break;
			}
		}
		else {
			standardHandle(new UnimplementedException("Reference types for const have not been implemented"));
		}
	}

	private static boolean isSimple(kdlParser.ExpressionContext xpr) {
		return xpr.value().size() == 1;
	}

	public Constant<?> pushConstant(String cname, LinedMethodVisitor lmv) {
		Constant c = owner.resolveConstant(cname);
		return pushConstant(c, lmv);
	}

	public Value pushValue(kdlParser.ValueContext val, LinedMethodVisitor lmv) {
		if(val.literal() != null) {
			return new Value(LITERAL, pushLiteral(val.literal(), lmv));
		}
		else if(val.CONSTNAME() != null) {
			return new Value(CONSTANT, pushConstant(val.CONSTNAME().toString(), lmv));
		}
		else if(val.VARNAME() != null) {
			LocalVariable lv = owner.getLocalVariable(val.VARNAME().toString());
			return new Value(VARIABLE, pushVariable(lv, lmv));
		}
		else if(val.arrayAccess() != null) {
			LocalVariable array = owner.getLocalVariable(val.arrayAccess().VARNAME().toString());
			lmv.visitVarInsn(ALOAD, array.localIndex);

			Value pushed = pushValue(val.arrayAccess().expression().value(0), lmv);
			// cause error if value within [ ] isn't an int
			if(pushed.toBaseType() != INT) {
				standardHandle(new IncompatibleTypeException("The input for an array access must be an integer"));
			}

			if(array.type.isBaseType()) {
				switch(array.type.toBaseType()) {
					case INT:
					case BOOLEAN:
						lmv.visitInsn(IALOAD);
						return new Value(LIMBO, new Limbo(array.type.toBaseType()));
					case STRING:
						lmv.visitInsn(AALOAD);
						return new Value(POINTER, new Pointer());
				}
			}
			else {
				lmv.visitInsn(AALOAD);
				return new Value(POINTER, new Pointer());
			}
		}
		else
			standardHandle(new UnimplementedException("ValueExpression kind missed"));
		return null;
	}

	@Override
	public void enterPkg(kdlParser.PkgContext ctx) {
		pkgName = ctx.PKG_NAME().getText();
	}

	private void push(Constant<?> c, LinedMethodVisitor lmv) {
		if(c.isBaseType())
			lmv.visitLdcInsn(c.value);
		else
			lmv.visitFieldInsn(GETSTATIC, owner.getClazz().internalNameString(), c.name, internalName(c.value.getClass()).stringOutput());
	}

	public int getPass() {
		return pass;
	}

	public void newPass() {
		pass++;
	}

	@Override
	public void enterSee(kdlParser.SeeContext ctx) {
		if(pass == 1)
			owner.addImport(new Import(ctx.QUALIFIED_NAME().toString()));
	}

	@Override
	public void enterClazz(final kdlParser.ClazzContext clazzCtx) {
		if(pass == 1) {
			pkgName = nonNull(pkgName);
			owner.setClassName(pkgName, clazzCtx.CLASSNAME().toString());
		}
	}

	@Override
	public void enterConstant(final kdlParser.ConstantContext ctx) {
		if(pass == 1) {
			Constant c = null;
			final String name = ctx.CONSTNAME().toString();

			switch(parseLiteralType(ctx.literal())) {
				case BOOLEAN:
					c = new Constant<>(name, parseBool(ctx.literal()));
					break;
				case INT:
					c = new Constant<>(name, parseNumber(ctx.literal()));
					break;
				case STRING:
					c = new Constant<>(name, parseStringLit(ctx.literal()));
					break;
			}

			if(!owner.addConstant(c))
				standardHandle(new IllegalArgumentException("The const name " + c.name + " was taken by another const with value " + owner.resolveConstant(c.name).value));
		}
	}

	private void storeValue(kdlParser.ValueContext val, LocalVariable target, LinedMethodVisitor lmv) {
		try {
			if(val.literal() != null) {
				if(target.type.equals(parseLiteralType(val.literal())))
					storeLiteral(val.literal(), target.localIndex, lmv);
				else
					throw new IncompatibleTypeException("Literal " + val.literal().getText() + INCOMPATIBLE + target);
			}
			else if(val.CONSTNAME() != null) {
				Constant c = owner.resolveConstant(val.CONSTNAME().toString());
				if(target.type.equals(c.internalObjectName()))
					storeConstant(c, target.localIndex, lmv);
				else
					throw new IncompatibleTypeException(c + INCOMPATIBLE + target);
			}
			else if(val.VARNAME() != null) {
				LocalVariable lv = owner.getLocalVariable(val.VARNAME().toString());
				if(lv.type.equals(target.type))
					storeVariable(lv, target, lmv);
				else
					throw new IncompatibleTypeException(lv + INCOMPATIBLE + target);
			}
			else if(val.arrayAccess() != null) {
				if(target.type.isArray()) {
					LocalVariable array = owner.getLocalVariable(val.arrayAccess().VARNAME().toString());
					lmv.visitVarInsn(ALOAD, array.localIndex);
					pushValue(val.arrayAccess().expression().value(0), lmv);
					if(array.type.isBaseType()) {
						switch(array.type.toBaseType()) {
							case INT:
							case BOOLEAN:
								lmv.visitInsn(IALOAD);
								lmv.visitVarInsn(ISTORE, target.localIndex);
								break;
							case STRING:
								lmv.visitInsn(AALOAD);
								lmv.visitVarInsn(ASTORE, target.localIndex);
								break;
						}
					}
					else {
						lmv.visitInsn(AALOAD);
						lmv.visitVarInsn(ASTORE, target.localIndex);
						return;
					}
				}
			}
			else
				throw new TokenNotFoundException("No recognizable value was found within value expression " + val.getText());
		} catch(IncompatibleTypeException | TokenNotFoundException e) {
			standardHandle(e);
		}
	}

	private BaseType pushExpression(kdlParser.ExpressionContext xpr, LinedMethodVisitor lmv) {
		Value val1 = pushValue(xpr.value(0), lmv);
		Value val2 = pushValue(xpr.value(1), lmv);
		Operator opr = Operator.match(xpr.operator().getText());
		ExpressionHandler.compute(val1, val2, opr, lmv);
		return INT;
	}

	private void storeExpression(kdlParser.ExpressionContext xpr, LocalVariable var, LinedMethodVisitor lmv) {
		BaseType result = pushExpression(xpr, lmv);
		if(var.isBaseType() && var.toBaseType() == result) {
			switch(result) {
				case INT:
				case BOOLEAN:
					lmv.visitVarInsn(ISTORE, var.localIndex);
					break;
				case STRING:
					lmv.visitVarInsn(ASTORE, var.localIndex);
			}
		}
		else
			standardHandle(new IncompatibleTypeException("The expression type of " + result + " did not match " + var));
	}

	private void parseVariableDeclaration(kdlParser.VariableDeclarationContext ctx, LinedMethodVisitor lmv) {
		final kdlParser.TypedVariableContext typedVar = ctx.typedVariable();
		NameAndType varNAT = parseTypedVariable(typedVar);
		LocalVariable var = new LocalVariable(owner.currentScope, varNAT.name, new InternalObjectName(varNAT.type));
		if(ctx.expression() != null) {
			if(isSimple(ctx.expression()))
				storeValue(ctx.expression().value(0), var, lmv);
			else
				storeExpression(ctx.expression(), var, lmv);
		}
		else
			storeDefault(var, lmv);
	}

	private InternalObjectName parseArrayType(kdlParser.ValueContext ctx) {
		if(ctx.arrayAccess() != null)
			return new InternalObjectName(owner.getLocalVariable(ctx.arrayAccess().VARNAME().toString()).type.inName, 0);
		else {
			standardHandle(new IncompatibleTypeException("This type was not an array type, so its element could not be discovered"));
			return null;
		}
	}

	public InternalObjectName parseType(kdlParser.ValueContext ctx) {
		if(ctx.literal() != null)
			return InternalName.match(parseLiteralType(ctx.literal())).object();
		else if(ctx.CONSTNAME() != null)
			return owner.resolveConstant(ctx.CONSTNAME().toString()).internalObjectName();
		else if(ctx.VARNAME() != null)
			return owner.getLocalVariable(ctx.VARNAME().toString()).type;
		else if(ctx.arrayAccess() != null)
			return owner.getLocalVariable(ctx.arrayAccess().VARNAME().toString()).type;
		else {
			standardHandle(new UnimplementedException("Unhandled kind of ValueExpression"));
			return null;
		}
	}

	private void parseVariableAssignment(kdlParser.VariableAssignmentContext ctx, LinedMethodVisitor lmv) {
		LocalVariable var = owner.getLocalVariable(ctx.VARNAME().toString());
		if(isSimple(ctx.expression()))
			storeValue(ctx.expression().value(0), var, lmv);
		else
			storeExpression(ctx.expression(), var, lmv);
	}

	private void parseMethodCall(kdlParser.MethodCallContext ctx, LinedMethodVisitor lmv) {
		String methodName = ctx.VARNAME().toString();
		if(ExternalMethodRouter.resolveMethod(methodName) != null) {
			MethodDef targetMethod = ExternalMethodRouter.resolveMethod(methodName);
			BestList<kdlParser.ValueContext> params = new BestList<>(ctx.parameterSet().expression(0).value());
			for(int i = 0; i < params.size(); i++) {
				if(parseType(params.get(i)).equals(targetMethod.paramTypes.get(i)))
					pushValue(params.get(i), lmv);
				else if(params.get(i).arrayAccess() != null && parseArrayType(params.get(i)).equals(targetMethod.paramTypes.get(i))) {
					pushValue(params.get(i), lmv);
				}
				else if(parseType(params.get(i)).isBaseType()) {
					pushValue(params.get(i), lmv);
					convertToString(parseType(params.get(i)), lmv);
				}
				else
					standardHandle(new IncompatibleTypeException("Parameter " + i + INCOMPATIBLE + parseType(params.get(i))));
			}
			ExternalMethodRouter.writeMethod(methodName, lmv, params);
		}
		else
			standardHandle(new UnimplementedException("Calls to custom methods have not been implemented"));
	}

	@Override
	public void enterRun(final kdlParser.RunContext ctx) {
		if(pass == 1) {
			owner.addMethodDef(MethodDef.MAIN);
		}
		else if(pass == 2) {
			final LinedMethodVisitor lmv = owner.defineMethod(MethodDef.MAIN, ctx.start.getLine() + 1);
			new LocalVariable(owner.currentScope, "args", new InternalObjectName(String.class, 1));
			Label methodStart = new Label();

			for(kdlParser.StatementContext statement : ctx.methodBody().statement()) {
				if(statement.variableDeclaration() != null) {
					parseVariableDeclaration(statement.variableDeclaration(), lmv);
				}
				else if(statement.variableAssignment() != null) {
					parseVariableAssignment(statement.variableAssignment(), lmv);
				}
				else if(statement.methodCall() != null) {
					parseMethodCall(statement.methodCall(), lmv);
				}
			}

			final Label ret = new Label();
			lmv.visitLabel(ret);
			lmv.visitLineNumber(ctx.start.getLine(), ret);
			lmv.visitInsn(Opcodes.RETURN);

			final Label methodEnd = new Label();
			lmv.visitLabel(methodEnd);
			for(LocalVariable lv : owner.currentScope.getVariables())
				lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
			lmv.visitMaxs(0, 0);
			lmv.visitEnd();
		}
	}

}
