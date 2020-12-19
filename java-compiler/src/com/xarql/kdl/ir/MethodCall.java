package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;

public class MethodCall implements Pushable, CommonText {

	public final MethodInvocation invocation;

	public MethodCall(Pushable source, kdl.MethodCallContext ctx, Actor actor) throws Exception {
		// get the method's id
		final String methodName = ctx.IDENTIFIER().getText();

		// determine which object is the source of the call
		if(source == null) {
			source = actor.unit.type;
		}

		// parse the args
		final BestList<Pushable> args = parseArguments(ctx.argumentSet(), actor);

		// build a method target and attempt to resolve it
		MethodTarget known = new MethodTarget(source.toInternalName(), methodName, argTypes(args), false);
		invocation = known.resolve(actor).withOwner(source).withArgs(args);
	}

	public static BestList<Pushable> parseArguments(kdl.ArgumentSetContext ctx, Actor actor) throws Exception {
		final BestList<Pushable> arguments = new BestList<>();
		if(ctx != null && ctx.expression().size() > 0) {
			for(kdl.ExpressionContext xpr : ctx.expression()) {
				Expression xpr1 = new Expression(actor.unit.type, xpr, actor);
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
