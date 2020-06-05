package main.com.xarql.kdl;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SourceListener extends kdlBaseListener {
	private final ClassCreator owner;

	public SourceListener(final ClassCreator owner) {
		this.owner = owner;
	}

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}

	@Override
	public void enterClazz(final kdlParser.ClazzContext clazzCtx) {
		owner.setClassName(clazzCtx.CLASSNAME().toString());
	}

	@Override
	public void enterConstant(final kdlParser.ConstantContext ctx) {
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
		owner.addConstant(c);
	}

	@Override
	public void enterRun(final kdlParser.RunContext ctx) {
		final MethodVisitor mv = owner.addMainMethod();

		// if the statement is a method call
		if(ctx.statement(0).methodCallStatement() != null) {
			final kdlParser.RegularMethodCallContext callContext = ctx.statement(0).methodCallStatement().methodCallChain().methodCall().regularMethodCall();

			final String methodName = callContext.VARNAME().toString();
			System.out.println("methodName: " + methodName);
			if(methodName.equals("print")) {
				final Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(7, l0);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
				final String constName = callContext.parameterSet().parameter(0).CONSTNAME().toString();
				System.out.println(constName);
				System.out.println(owner.hasConstant(constName));
				mv.visitLdcInsn(owner.constant(constName).value.toString());
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
				final Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLineNumber(8, l1);
				mv.visitInsn(Opcodes.RETURN);

				final Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitLocalVariable("args", "[Ljava/lang/String;", null, l0, l2, 0);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
		}
	}

}
