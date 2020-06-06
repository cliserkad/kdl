package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlBaseListener;
import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.CommonNames;
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
			final kdlParser.LiteralContext literal = ctx.literal();
			if(literal.STRING() != null)
				c = new Constant<>(name, crush(literal.STRING().toString()));
			else if(literal.bool() != null) {
				if(literal.bool().TRUE() != null)
					c = new Constant<>(name, true);
				else
					c = new Constant<>(name, false);
			}
			else if(literal.number() != null) {
				try {
					c = new Constant<>(name, Integer.valueOf(literal.number().getText()));
				} catch(final NumberFormatException nfe) {
					System.err.println("Couldn't convert the const " + literal.number().getText() + " to an int.");
					c = new Constant<>(name, INT.base.getDefaultValue());
				}
			}
			else {
				throw new IllegalArgumentException("Type of const " + c.name + " could not be inferred. It appeared as " + ctx.getText());
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
					if(typedVar.type().basetype() != null) {
						final String baseType = typedVar.type().basetype().getText();

						// handle "string" base type
						if(baseType.equals(BASETYPE_STRING)) {
							LocalVariable var = new LocalVariable(owner.currentScope, typedVar.VARNAME().toString(), STRING_ION);
							if(varDec.literal().STRING() != null) {
								lmv.visitLdcInsn(crush(varDec.literal().STRING().toString()));
							}
							else
								throw new IllegalArgumentException("Variable declaration with type string had right side argument of " + varDec.literal().getText());
							lmv.visitVarInsn(ASTORE, var.localIndex);
						}
						else
							System.out.println("baseType was actually " + baseType);
					}
					else
						System.out.println("basetype shouldn't be null");
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
			if(pc.literal().STRING() != null)
				return crush(pc.literal().STRING().getText());
			else
				return pc.literal().getText();
		}
		else if(pc.CONSTNAME() != null) {
			return owner.resolveConstant(pc.CONSTNAME().toString()).value.toString();
		}
		else
			throw new IllegalArgumentException("This cannot be used for variables");
	}

}
