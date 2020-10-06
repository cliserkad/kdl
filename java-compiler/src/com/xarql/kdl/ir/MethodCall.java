package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;

public class MethodCall extends BasePushable implements CommonText {

	public final MethodInvocation invocation;

	public MethodCall(kdl.MethodCallContext ctx, Actor actor) throws Exception {
		// parse methodCall alone
		final String methodName = ctx.VARNAME(ctx.VARNAME().size() - 1).getText();

		// determine which class owns the method being called
		final Pushable source;
		final InternalName owner;
		boolean isStatic;
		if(ctx.CLASSNAME() != null) {
			source = null;
			owner = actor.unit.resolveAgainstImports(ctx.CLASSNAME().getText());
			isStatic = true;
		} else if(ctx.VARNAME().size() > 1) {
			source = actor.unit.getLocalVariable(ctx.VARNAME(0).getText());
			owner = source.toInternalName();
			isStatic = false;
		} else {
			source = null;
			owner = new InternalName(actor.unit.getClazz());
			isStatic = false;
		}

		final BestList<Pushable> args = parseArguments(ctx.parameterSet(), actor);

		MethodTarget known = new MethodTarget(owner, methodName, argTypes(args), isStatic);
		invocation = known.resolve(actor).withOwner(source).withArgs(args);
	}

	public MethodCall(kdl.MethodCallStatementContext ctx, Actor actor) throws Exception {
		this(ctx.methodCall(), actor);
	}

	public static BestList<Pushable> parseArguments(kdl.ParameterSetContext ctx, Actor actor) throws Exception {
		final BestList<Pushable> arguments = new BestList<>();
		if(ctx != null && ctx.expression().size() > 0) {
			for(kdl.ExpressionContext xpr : ctx.expression()) {
				Expression xpr1 = new Expression(xpr, actor);
				arguments.add(xpr1);
			}
		}
		return arguments;
	}

	public BestList<InternalName> argTypes(BestList<Pushable> args) {
		final BestList<InternalName> argTypes = new BestList<>();
		for(Pushable arg : args)
			argTypes.add(arg.toInternalName());
		return argTypes;
	}

	@Override
	public MethodCall push(final Actor actor) throws Exception {
		invocation.push(actor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return invocation.header.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return invocation.header.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return invocation.header.toBaseType();
	}

	@Override
	public String toString() {
		return "MethodCall --> {\n\tMethod --> " + invocation.header + arguments() + "\n}";
	}

	private String arguments() {
		String out = "\n\tCalculable --> {";
		for(Pushable arg : invocation.args)
			out += "\n\t\t" + arg.toString().replace("\n", "\n\t\t");
		out += "\n\t}";
		return out;
	}

}
