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
	private final ClassCreator owner;

	private String pkgName;

	// pass 0 does nothing
	// pass 1 collects imports, classname, and constants, methodNames
	// pass 2 defines methods
	private int pass;

	public SourceListener(final ClassCreator owner) {
		this.owner = owner;
		pass = 0;
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

	public static void push(LocalVariable lv, MethodVisitor mv) {
		if(lv.type.isBaseType() && lv.type.toBaseType() != STRING) {
			if(lv.type.toBaseType() == INT) {
				mv.visitVarInsn(ILOAD, lv.localIndex);
			}
			else if(lv.type.toBaseType() == BOOLEAN) {
				mv.visitVarInsn(ILOAD, lv.localIndex);
			}
			else
				throw new IllegalArgumentException("Something went wrong in push(LocalVariable, MethodVisitor)");
		}
		else {
			mv.visitVarInsn(ALOAD, lv.localIndex);
		}
	}

	private static String parseStringLit(kdlParser.LiteralContext literal) {
		if(literal.STRING_LIT() != null) {
			return crush(literal.STRING_LIT().toString());
		}
		else
			throw new NullPointerException("Couldn't parse string from literal " + literal.getText() + " because non was present");
	}

	private static boolean parseBool(kdlParser.BoolContext bool) {
		if(bool.TRUE() != null)
			return true;
		else
			return false;
	}

	private static int parseNumber(kdlParser.NumberContext number) {
		try {
			return Integer.valueOf(number.getText());
		} catch(NullPointerException npe) {
			TokenNotFoundException notFound = new TokenNotFoundException("The expected NumberContext wasn't provided");
			System.err.println();
			notFound.printStackTrace();
			exit(1);
			return 0;
		} catch(final NumberFormatException nfe) {
			return 0;
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

	public static void push(kdlParser.LiteralContext literal, LinedMethodVisitor lmv) throws TokenNotFoundException {
		BaseType type = parseLiteralType(literal);
		switch(type) {
			case BOOLEAN:
				lmv.visitLdcInsn(parseBool(literal.bool()));
				break;
			case INT:
				lmv.visitLdcInsn(parseNumber(literal.number()));
				break;
			case STRING:
				lmv.visitLdcInsn(parseStringLit(literal));
				break;
			default:
				throw new TokenNotFoundException("Couldn't resolve a token to its Literal type. BaseType switch fell through. Was provided " + literal.getText());
		}
	}

	private static void convertToString(BaseType base, LinedMethodVisitor lmv) {
		final MethodDef stringValueOf = new MethodDef(MethodDef.Type.MTD, "valueOf", list(base.toInternalName().object()), new ReturnValue(String.class), ACC_PUBLIC + ACC_STATIC);
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
		try {
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
				throw new UnimplementedException("Loading a Literal type");
		} catch(UnimplementedException ue) {
			System.err.println(ue);
			ue.printStackTrace();
			exit(1);
			return;
		}
	}

	private static void storeConstant(Constant<?> c, int localIndex, LinedMethodVisitor lmv) {
		try {
			if(c.isBaseType()) {
				lmv.visitLdcInsn(c.value);
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
				throw new UnimplementedException("Reference types for const have not been implemented");
			}
		} catch(UnimplementedException ue) {
			System.err.println(ue.getMessage());
			ue.printStackTrace();
			exit(1);
			return;
		}
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

	private static void loadVariable(LocalVariable lv, LinedMethodVisitor lmv) throws UnimplementedException {
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
					c = new Constant<>(name, parseBool(ctx.literal().bool()));
					break;
				case INT:
					c = new Constant<>(name, parseNumber(ctx.literal().number()));
					break;
				case STRING:
					c = new Constant<>(name, parseStringLit(ctx.literal()));
					break;
			}

			if(!owner.addConstant(c))
				throw new IllegalArgumentException("The const name " + c.name + " was taken by another const with value " + owner.resolveConstant(c.name).value);
		}
	}

	private void parseVariableDeclaration(kdlParser.VariableDeclarationContext ctx, LinedMethodVisitor lmv) throws TokenNotFoundException, UnimplementedException, IncompatibleTypeException {
		final kdlParser.TypedVariableContext typedVar = ctx.typedVariable();
		NameAndType varNAT = parseTypedVariable(typedVar);
		LocalVariable var = new LocalVariable(owner.currentScope, varNAT.name, new InternalObjectName(varNAT.type));
		if(ctx.literal() != null) {
			storeLiteral(ctx.literal(), var.localIndex, lmv);
		}
		else if(ctx.CONSTNAME() != null) {
			Constant c = owner.resolveConstant(ctx.CONSTNAME().toString());
			storeConstant(c, var.localIndex, lmv);
		}
		else if(ctx.VARNAME() != null) {
			LocalVariable otherVar = owner.currentScope.getVariable(ctx.VARNAME().toString());
			storeVariable(otherVar, var, lmv);
		}
	}

	@Override
	public void enterRun(final kdlParser.RunContext ctx) {
		try {
			if(pass == 1) {
				owner.addMethodDef(MethodDef.MAIN);
			}
			else if(pass == 2) {
				final LinedMethodVisitor lmv = owner.defineMethod(MethodDef.MAIN, ctx.start.getLine() + 1);
				new LocalVariable(owner.currentScope, "args", new InternalObjectName(String.class, 1));
				Label methodStart = new Label();

				for(kdlParser.StatementContext statement : ctx.statement()) {
					if(statement.variableDeclaration() != null) {
						try {
							parseVariableDeclaration(statement.variableDeclaration(), lmv);
						} catch(UnimplementedException | IncompatibleTypeException e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
							exit(1);
							return;
						}
					}
					else if(statement.variableAssignment() != null) {
						final kdlParser.VariableAssignmentContext varAssign = statement.variableAssignment();
						LocalVariable target = owner.getLocalVariable(varAssign.VARNAME(0).toString());
						if(varAssign.literal() != null)
							storeLiteral(varAssign.literal(), target.localIndex, lmv);
						else if(varAssign.CONSTNAME() != null)
							storeConstant(owner.resolveConstant(varAssign.CONSTNAME().toString()), target.localIndex, lmv);
						else
							storeVariable(owner.currentScope.getVariable(varAssign.VARNAME(1).toString()), target, lmv);
					}
					else if(statement.methodCall() != null) {
						final kdlParser.MethodCallContext methodCall = statement.methodCall();
						final kdlParser.ParameterSetContext paramSet = methodCall.parameterSet();
						final String methodName = methodCall.VARNAME().toString();
						for(kdlParser.ParameterContext param : paramSet.parameter()) {
							if(param.CONSTNAME() != null || param.literal() != null) {
								lmv.visitLdcInsn(paramToText(param));
							}
							else {
								InternalObjectName paramType = ExternalMethodRouter.resolveMethod(methodName).paramTypes.get(0);
								LocalVariable operand = owner.getLocalVariable(param.VARNAME().toString());
								push(operand, lmv);
								if(!operand.type.equals(paramType)) {
									if(operand.type.isBaseType())
										convertToString(operand.type.toBaseType(), lmv);
									else
										throw new IllegalArgumentException("The type " + operand.type + " is incompatible with param type " + paramType);
								}
								else
									lmv.visitVarInsn(ALOAD, operand.localIndex);
							}
						}
						ExternalMethodRouter.writeMethod(methodName, lmv);
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
		} catch(TokenNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public String paramToText(kdlParser.ParameterContext pc) {
		if(pc.literal() != null) {
			switch(parseLiteralType(pc.literal())) {
				case BOOLEAN:
					return pc.literal().bool().toString();
				case INT:
					return pc.literal().number().toString();
				case STRING:
					return parseStringLit(pc.literal());
			}
		}
		else if(pc.CONSTNAME() != null) {
			return owner.resolveConstant(pc.CONSTNAME().toString()).value.toString();
		}
		else
			throw new IllegalArgumentException("This value could not be known at compile time");
		return null;
	}

}
