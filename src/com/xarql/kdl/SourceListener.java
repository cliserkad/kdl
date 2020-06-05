package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlBaseListener;
import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.PrintStream;

import static com.xarql.kdl.names.NameFormats.internalName;
import static com.xarql.kdl.names.NameFormats.internalObjectName;

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

	private static Label addPrintIns(MethodVisitor mv, String out, int lineNumber) {
		final Label print = new Label();
		mv.visitLabel(print);
		mv.visitLineNumber(lineNumber, print);
		mv.visitFieldInsn(GETSTATIC, internalName(System.class), "out", internalObjectName(PrintStream.class));
		mv.visitLdcInsn(out);
		MethodDef md = new MethodDef(MethodDef.Type.MTD, PRINTLN, STRING_PARAM, VOID, ACC_PUBLIC + ACC_STATIC);
		mv.visitMethodInsn(INVOKEVIRTUAL, internalName(PrintStream.class), PRINTLN, md.descriptor(), false);
		return print;
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
			final Constant c = new Constant(ctx.CONSTNAME().toString());
			final kdlParser.LiteralContext literal = ctx.literal();
			if(literal.STRING() != null)
				c.value = new StringValue(crush(literal.STRING().toString()));
			else if(literal.bool() != null) {
				if(literal.bool().TRUE() != null)
					c.value = new BooleanValue(true);
				else
					c.value = new BooleanValue(false);
			}
			else if(literal.number() != null) {
				try {
					c.value = new IntegerValue(Integer.valueOf(literal.number().getText()));
				} catch(final NumberFormatException nfe) {
					System.err.println("Couldn't convert the const " + literal.number().getText() + " to an int.");
					c.value = new IntegerValue(0);
				}
			}
			else
				throw new IllegalArgumentException("Type of const " + c.name + " could not be inferred. It appeared as " + ctx.getText());
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
			final MethodVisitor mv = owner.defineMethod(MethodDef.MAIN);
			Label methodStart = null;
			BestList<Label> labels = new BestList<>();

			for(kdlParser.StatementContext statement : ctx.statement()) {
				// if the statement is a method call
				if(statement.methodCallStatement() != null) {
					// grab all possible beginning calls
					final kdlParser.RegularMethodCallContext regCall = statement.methodCallStatement().methodCallChain().methodCall().regularMethodCall();
					final kdlParser.StaticMethodCallContext staticCall = statement.methodCallStatement().methodCallChain().methodCall().staticMethodCall();
					final kdlParser.ObjectiveMethodCallContext objectCall = statement.methodCallStatement().methodCallChain().methodCall().objectiveMethodCall();

					if(regCall != null) {
						final String methodName = regCall.VARNAME().toString();
						if(methodName.equals(PRINTLN)) {
							int lineNum = regCall.start.getLine();

							// add all constant and literal parameters to the print call
							String str = "";
							for(kdlParser.ParameterContext pc : regCall.parameterSet().parameter()) {
								if(pc.literal() != null) {
									if(pc.literal().STRING() != null)
										str += crush(pc.literal().STRING().getText());
									else
										str += pc.literal().getText();
								}
								else if(pc.CONSTNAME() != null) {
									str += owner.resolveConstant(pc.CONSTNAME().toString()).value.toString();
								}
							}

							Label result = addPrintIns(mv, str, lineNum);
							if(methodStart == null)
								methodStart = result;
							else
								labels.add(result);
						}
					}
					else if(staticCall != null) {
						Import imp = owner.resolveClassName(staticCall.CLASSNAME().toString());
					}
				}
			}

			final Label ret = new Label();
			mv.visitLabel(ret);
			mv.visitLineNumber(ctx.start.getLine(), ret);
			mv.visitInsn(Opcodes.RETURN);

			final Label methodEnd = new Label();
			mv.visitLabel(methodEnd);
			mv.visitLocalVariable("args", internalObjectName(String.class), null, methodStart, methodEnd, 0);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}

}
