package main.com.xarql.kdl;

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
	public void enterLiteral(final kdlParser.LiteralContext ctx) {
		if(ctx.getParent() instanceof kdlParser.ConstantContext) {
			final kdlParser.ConstantContext parent = (kdlParser.ConstantContext) ctx.getParent();
			final Constant c = new Constant(parent.getChild(1).toString());
			if(ctx.STRING() != null)
				c.value = new StringValue(crush(ctx.STRING().toString()));
			else if(ctx.bool() != null) {
				if(ctx.bool().TRUE() != null)
					c.value = new BooleanValue(true);
				else
					c.value = new BooleanValue(false);
			}
			else if(ctx.number() != null) {
				try {
					c.value = new IntegerValue(Integer.valueOf(ctx.number().getText()));
				} catch(final NumberFormatException nfe) {
					System.err.println("Couldn't convert the const " + ctx.number().getText() + " to an int.");
					c.value = new IntegerValue(0);
				}
			}
			else
				throw new IllegalArgumentException("Type of const " + c.name + " could not be inferred. It appeared as " + ctx.getText());
			owner.addConstant(c);
		}
	}

	@Override
	public void enterRun(final kdlParser.RunContext ctx) {
		for(final kdlParser.StatementContext state : ctx.statement()) {

		}
	}

}
