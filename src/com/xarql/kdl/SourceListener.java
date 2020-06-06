package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlBaseListener;
import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SourceListener extends kdlBaseListener implements Opcodes, CommonNames {
	private final ClassCreator owner;

	// pass 0 does nothing
	// pass 1 collects imports, classname, and constants, methodNames
	// pass 2 defines methods
	private int pass;

	public SourceListener(final ClassCreator owner) {
		this.owner = owner;
		pass = 0;
	}

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}

	public static void push(String str, MethodVisitor mv) {
		mv.visitLdcInsn(str);
	}

	public static void push(int val, MethodVisitor mv) {
		mv.visitLdcInsn(val);
	}

	public static void push(LocalVariable lv, MethodVisitor mv) {
		if(lv.type.equals(INT.object())) {
			mv.visitVarInsn(ILOAD, lv.localIndex);
		}
		else if(lv.type.equals(BOOLEAN.object())) {
			mv.visitVarInsn(ILOAD, lv.localIndex);
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
		} catch(final NumberFormatException nfe) {
			System.err.println("Couldn't convert the literal " + number.getText() + " to an int.");
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
			type = BOOLEAN;
		}
		else if(ctx.type().basetype().INT() != null) {
			type = INT;
		}
		else if(ctx.type().basetype().STRING() != null) {
			type = STRING_IN;
		}
		else
			throw new IllegalArgumentException("Couldn't resolve type of variable");

		return new NameAndType(name, type);
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
		if(pass == 1)
			owner.setClassName(clazzCtx.CLASSNAME().toString());
	}

	@Override
	public void enterConstant(final kdlParser.ConstantContext ctx) {
		if(pass == 1) {
			Constant c = null;
			final String name = ctx.CONSTNAME().toString();

			switch(parseLiteralType(ctx.literal())) {
				case BOOLEAN:
					c = new Constant<>(name, parseBool(ctx.literal().bool()));
				case INT:
					c = new Constant<>(name, parseNumber(ctx.literal().number()));
				case STRING:
					c = new Constant<>(name, parseStringLit(ctx.literal()));
			}

			if(!owner.addConstant(c))
				throw new IllegalArgumentException("The const name " + c.name + " was taken by another const with value " + owner.resolveConstant(c.name).value);
		}
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

			for(kdlParser.StatementContext statement : ctx.statement()) {
				if(statement.variableDeclaration() != null) {
					final kdlParser.VariableDeclarationContext varDec = statement.variableDeclaration();
					final kdlParser.TypedVariableContext typedVar = varDec.typedVariable();
					NameAndType varNAT = parseTypedVariable(typedVar);
					if(varNAT.type.equals(BOOLEAN))
						lmv.visitLdcInsn(parseBool(varDec.literal().bool()));
					else if(varNAT.type.equals(INT))
						lmv.visitLdcInsn(parseNumber(varDec.literal().number()));
					else if(varNAT.type.equals(STRING_IN))
						lmv.visitLdcInsn(parseStringLit(varDec.literal()));
					else
						throw new IllegalArgumentException("Variable declaration with type string had right side argument of " + varDec.literal().getText());
					LocalVariable var = new LocalVariable(owner.currentScope, varNAT.name, new InternalObjectName(varNAT.type));
					lmv.visitVarInsn(ASTORE, var.localIndex);
				}
				else if(statement.methodCall() != null) {
					final kdlParser.MethodCallContext methodCall = statement.methodCall();
					final kdlParser.ParameterSetContext paramSet = methodCall.parameterSet();
					for(kdlParser.ParameterContext param : paramSet.parameter()) {
						if(param.CONSTNAME() != null || param.literal() != null) {
							push(paramToText(param), lmv);
						}
						else {
							push(owner.getLocalVariable(param.VARNAME().toString()), lmv);
						}
					}
					ExternalMethodRouter.writeMethod(methodCall.VARNAME().toString(), lmv);
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
