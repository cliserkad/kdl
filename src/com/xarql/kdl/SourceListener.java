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
	private final ExpressionHandler xprHandler;
	private final BestList<String>  constantNames = new BestList<>();

	private String pkgName;

	// pass 1 collects imports, classname, and constant names, methodNames
	// pass 2 assigns values to constants
	// pass 3 defines methods
	private int pass;

	public SourceListener(final ClassCreator owner) {
		this.owner = owner;
		pass = 0;
		xprHandler = new ExpressionHandler(this);
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

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}

	public static Literal<?> pushLiteral(final kdlParser.LiteralContext literal, LinedMethodVisitor lmv) {
		if(literal.bool() != null)
			return pushLiteral(new Literal<>(parseBool(literal)), lmv);
		else if(literal.number() != null) {
			return pushLiteral(new Literal<>(parseNumber(literal)), lmv);
		}
		else if(literal.STRING_LIT() != null) {
			return pushLiteral(new Literal<>(parseStringLit(literal)), lmv);
		}
		else {
			standardHandle(new UnimplementedException(SWITCH_BASETYPE));
			return null;
		}
	}

	public static Literal<?> pushLiteral(final Literal literal, LinedMethodVisitor lmv) {
		lmv.visitLdcInsn(literal.value);
		return literal;
	}

	public static Variable pushVariable(Variable lv, MethodVisitor mv) {
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

	public static boolean parseBool(kdlParser.BoolContext bool) {
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

	public static BaseType parseLiteralType(kdlParser.LiteralContext lit) {
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

	private static void storeVariable(Variable source, Variable target, LinedMethodVisitor lmv) {
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

	private static void loadVariable(Variable lv, LinedMethodVisitor lmv) {
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

	public Constant<?> pushConstant(Constant<?> c, LinedMethodVisitor lmv) {
		if(c.isBaseType())
			lmv.visitLdcInsn(c.value);
		else
			lmv.visitFieldInsn(GETSTATIC, owner.getClazz().internalNameString(), c.name, internalName(c.value.getClass()).stringOutput());
		return c;
	}

	private void storeConstant(Constant<?> c, int localIndex, LinedMethodVisitor lmv) {
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

	private static boolean isSimpleXpr(kdlParser.ExpressionContext xpr) {
		return xpr.value().size() == 1;
	}

	public static Literal<?> parseLiteral(kdlParser.LiteralContext ctx) {
		switch(parseLiteralType(ctx)) {
			case INT:
				return new Literal<>(parseNumber(ctx));
			case BOOLEAN:
				return new Literal<>(parseBool(ctx));
			case STRING:
				return new Literal<>(parseStringLit(ctx));
			default:
				standardHandle(new UnimplementedException(SWITCH_BASETYPE));
				return null;
		}
	}

	private static boolean isSimpleXpr(kdlParser.CompileTimeExpressionContext ctx) {
		return ctx.literal(0) != null && ctx.CONSTNAME().size() == 0;
	}

	private static Operator parseOperator(kdlParser.OperatorContext ctx) {
		return Operator.match(ctx.getText());
	}

	public Value pushValue(Value val, LinedMethodVisitor lmv) {
		if(val.valueType == LITERAL)
			return new Value(LITERAL, pushLiteral((Literal) val.content, lmv));
		else if(val.valueType == CONSTANT)
			return new Value(CONSTANT, pushConstant((Constant) val.content, lmv));
		else if(val.valueType == VARIABLE)
			return new Value(VARIABLE, pushVariable((Variable) val.content, lmv));
		else if(val.valueType == ARRAY_ACCESS) {
			final ArrayAccess arrayAccess = (ArrayAccess) val.content;
			final Variable array = arrayAccess.var;
			lmv.visitVarInsn(ALOAD, array.localIndex);

			BaseType type = pushExpression(arrayAccess.index, lmv);
			// cause error if value within [ ] isn't an int
			if(type != INT) {
				standardHandle(new IncompatibleTypeException("The input for an array access must be an integer"));
			}

			if(array.type.isBaseType()) {
				switch(array.type.toBaseType()) {
					case INT:
					case BOOLEAN:
						lmv.visitInsn(IALOAD);
						return new Value(STACK, new StackValue(array.type.toBaseType()));
					case STRING:
						lmv.visitInsn(AALOAD);
						return new Value(POINTER, STRING_IN);
				}
			}
			else {
				lmv.visitInsn(AALOAD);
				return new Value(POINTER, new Pointer(array.type));
			}
		}
		else
			standardHandle(new UnimplementedException("ValueExpression kind missed"));
		return null;
	}

	private Expression parseExpression(kdlParser.ExpressionContext xpr) {
		Value val1 = parseValue(xpr.value(0));
		Value val2 = parseValue(xpr.value(1));
		if(xpr.operator() != null) {
			Operator opr = parseOperator(xpr.operator());
			return new Expression(val1, val2, opr);
		}
		else {
			return new Expression(val1);
		}
	}

	public BaseType pushExpression(Expression xpr, LinedMethodVisitor lmv) {
		return xprHandler.compute(xpr, lmv);
	}

	public BaseType pushExpression(kdlParser.ExpressionContext xpr, LinedMethodVisitor lmv) {
		Value val1 = parseValue(xpr.value(0));
		Value val2 = parseValue(xpr.value(1));
		if(xpr.operator() != null) {
			Operator opr = parseOperator(xpr.operator());
			return xprHandler.compute(new Expression(val1, val2, opr), lmv);
		}
		else {
			pushValue(val1, lmv);
			return val1.toBaseType();
		}
	}

	private void storeExpression(kdlParser.ExpressionContext ctx, Variable var, LinedMethodVisitor lmv) {
		Expression xpr = new Expression(parseValue(ctx.value(0)), parseValue(ctx.value(1)), parseOperator(ctx.operator()));
		storeExpression(xpr, var, lmv);
	}

	private void storeExpression(Expression xpr, Variable var, LinedMethodVisitor lmv) {
		BaseType result = pushExpression(xpr, lmv);
		if(var.isBaseType() && var.toBaseType() == result) {
			switch(result) {
				case INT:
				case BOOLEAN:
					lmv.visitVarInsn(ISTORE, var.localIndex);
					return;
				case STRING:
					lmv.visitVarInsn(ASTORE, var.localIndex);
					return;
			}
		}
		else
			standardHandle(new IncompatibleTypeException("The expression type of " + result + " did not match " + var));
	}

	public Constant<?> pushConstant(String cname, LinedMethodVisitor lmv) {
		Constant c = owner.getConstant(cname);
		return pushConstant(c, lmv);
	}

	public Value parseValue(kdlParser.ValueContext val) {
		if(val == null)
			return null;
		else if(val.literal() != null)
			return new Value(LITERAL, parseLiteral(val.literal()));
		else if(val.CONSTNAME() != null)
			return new Value(CONSTANT, owner.getConstant(val.CONSTNAME().getText()));
		else if(val.VARNAME() != null) {
			Variable var = owner.getLocalVariable(val.VARNAME().toString());
			return new Value(VARIABLE, var);
		}
		else if(val.arrayAccess() != null) {
			Variable array = owner.getLocalVariable(val.arrayAccess().VARNAME().toString());
			return new Value(VARIABLE, array);
		}
		else
			standardHandle(new UnimplementedException("ValueExpression kind missed"));
		return null;
	}

	public Value pushValue(kdlParser.ValueContext val, LinedMethodVisitor lmv) {
		final Value out = parseValue(val);
		switch(out.valueType) {
			case LITERAL:
				pushLiteral((Literal) out.content, lmv);
				break;
			case CONSTANT:
				Constant c = (Constant) out.content;
				pushConstant(c.name, lmv);
				break;
			case VARIABLE:
			case POINTER:
				Variable var = (Variable) out.content;
				pushVariable(var, lmv);
				break;
			case STACK:
				pushLiteral(new Literal(out.content), lmv);
				break;
			default:
				standardHandle(new UnimplementedException(SWITCH_VALUETYPE));
		}
		return out;
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
		// collect names
		if(pass == 1) {
			final String name = ctx.CONSTNAME().toString();
			if(!constantNames.contains(name))
				constantNames.add(name);
			else
				standardHandle(new IllegalArgumentException("The const named " + name + " was taken by another const with value " + owner.getConstant(name).value));
		}
		else if(pass == 2) {
			String name = constantNames.get(0);
			if(isSimpleXpr(ctx.compileTimeExpression())) {
				if(ctx.compileTimeExpression().literal().size() > 1) {
					final Literal<?> value = ExpressionHandler.evaluateLiterals(ctx.compileTimeExpression());
					owner.addConstant(new Constant<>(name, value.value));
					constantNames.remove(0);
				}
				else {
					// add constant with the value of the first literal
					Constant c = new Constant(name, parseLiteral(ctx.compileTimeExpression().literal(0)).value);
					owner.addConstant(c);
					constantNames.remove(0);
				}
			}
		}
	}

	private void storeValue(kdlParser.ValueContext val, Variable target, LinedMethodVisitor lmv) {
		try {
			if(val.literal() != null) {
				if(target.type.equals(parseLiteralType(val.literal())))
					storeLiteral(val.literal(), target.localIndex, lmv);
				else
					throw new IncompatibleTypeException("Literal " + val.literal().getText() + INCOMPATIBLE + target);
			}
			else if(val.CONSTNAME() != null) {
				Constant c = owner.getConstant(val.CONSTNAME().toString());
				if(target.type.equals(c.toInternalObjectName()))
					storeConstant(c, target.localIndex, lmv);
				else
					throw new IncompatibleTypeException(c + INCOMPATIBLE + target);
			}
			else if(val.VARNAME() != null) {
				Variable lv = owner.getLocalVariable(val.VARNAME().toString());
				if(lv.type.equals(target.type))
					storeVariable(lv, target, lmv);
				else
					throw new IncompatibleTypeException(lv + INCOMPATIBLE + target);
			}
			else if(val.arrayAccess() != null) {
				if(target.type.isArray()) {
					Variable array = owner.getLocalVariable(val.arrayAccess().VARNAME().toString());
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


	private void consumeVariableDeclaration(kdlParser.VariableDeclarationContext ctx, LinedMethodVisitor lmv) {
		final kdlParser.TypedVariableContext typedVar = ctx.typedVariable();
		NameAndType varNAT = parseTypedVariable(typedVar);
		Variable var = new Variable(owner.currentScope, varNAT.name, new InternalObjectName(varNAT.type));
		if(ctx.expression() != null) {
			if(isSimpleXpr(ctx.expression()))
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
			return InternalName.match(parseLiteralType(ctx.literal())).toInternalObjectName();
		else if(ctx.CONSTNAME() != null)
			return owner.getConstant(ctx.CONSTNAME().toString()).toInternalObjectName();
		else if(ctx.VARNAME() != null)
			return owner.getLocalVariable(ctx.VARNAME().toString()).type;
		else if(ctx.arrayAccess() != null)
			return owner.getLocalVariable(ctx.arrayAccess().VARNAME().toString()).type;
		else {
			standardHandle(new UnimplementedException("Unhandled kind of ValueContext"));
			return null;
		}
	}

	public Expression parseOperatorAssign(Variable target, kdlParser.OperatorAssignContext ctx) {
		Operator opr = Operator.match(ctx.operator().getText());
		Value val = parseValue(ctx.value());
		return new Expression(new Value(VARIABLE, target), val, opr);
	}

	private void consumeVariableAssignment(kdlParser.VariableAssignmentContext ctx, LinedMethodVisitor lmv) {
		Variable var = owner.getLocalVariable(ctx.VARNAME().toString());
		if(ctx.assignment().operatorAssign() != null) {
			Expression xpr = parseOperatorAssign(var, ctx.assignment().operatorAssign());
			storeExpression(xpr, var, lmv);
		}
		else if(isSimpleXpr(ctx.assignment().expression()))
			storeValue(ctx.assignment().expression().value(0), var, lmv);
		else
			storeExpression(ctx.assignment().expression(), var, lmv);
	}

	private void consumeMethodCall(kdlParser.MethodCallContext ctx, LinedMethodVisitor lmv) {
		String methodName = ctx.VARNAME().toString();
		if(ExternalMethodRouter.resolveMethod(methodName) != null) {
			JavaMethodDef targetMethod = ExternalMethodRouter.resolveMethod(methodName);
			BestList<kdlParser.ExpressionContext> params = new BestList<>(ctx.parameterSet().expression());
			for(int i = 0; i < params.size(); i++) {
				if(parseType(params.get(i).value(0)).equals(targetMethod.paramTypes.get(i)))
					pushExpression(params.get(i), lmv);
				else if(params.get(i).value(0).arrayAccess() != null && parseArrayType(params.get(i).value(0)).equals(targetMethod.paramTypes.get(i))) {
					if(isSimpleXpr(params.get(i))) {
						pushVariable(owner.getLocalVariable(params.get(i).value(0).arrayAccess().VARNAME().getText()), lmv);
						pushExpression(params.get(i).value(0).arrayAccess().expression(), lmv);
						lmv.visitInsn(AALOAD);
					}
					else {
						System.err.println("I don't know what this error was supposed to be sorry.");
					}
				}
				else if(targetMethod.paramTypes.get(i).equals(STRING_ION)) {
					if(isSimpleXpr(params.get(i))) {
						pushValue(parseValue(params.get(i).value(0)), lmv);
						convertToString(parseType(params.get(i).value(0)), lmv);
					}
					else {
						BaseType type = pushExpression(params.get(i), lmv);
						convertToString(type.toInternalObjectName(), lmv);
					}
				}
				else
					standardHandle(new IncompatibleTypeException("Parameter " + i + INCOMPATIBLE + parseType(params.get(i).value(0))));
			}
			ExternalMethodRouter.writeMethod(methodName, lmv, params);
		}
		else
			standardHandle(new UnimplementedException("Calls to custom methods have not been implemented"));
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

	@Override
	public void enterRun(final kdlParser.RunContext ctx) {
		if(pass == 2) {
			owner.addMethodDef(JavaMethodDef.MAIN.withOwner(owner));
		}
		else if(pass == 3) {
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
		}
	}

}
