package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.TypeDescriptor;

public class MethodCall implements Pushable, CommonText {

	public final MethodInvocation invocation;

	public MethodCall(Type parent, kdl.MethodCallContext ctx, Actor actor) throws Exception {
		// get the method's id
		final String methodName = ctx.IDENTIFIER().getText();

		// determine which object is the source of the call
		if(parent == null)
			parent = actor.unit.type;

		// parse the args
		final BestList<Pushable> args = parseArguments(ctx.argumentSet(), actor);

		// build a method target and attempt to resolve it
		MethodTarget known = new MethodTarget(parent, methodName, argTypes(args), false);
		invocation = known.resolve(actor).withArgs(args);
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

	public BestList<TypeDescriptor> argTypes(BestList<Pushable> args) {
		final BestList<TypeDescriptor> argTypes = new BestList<>();
		for(Pushable arg : args)
			argTypes.add(arg.toTypeDescriptor());
		return argTypes;
	}

	@Override
	public MethodCall push(final Actor actor) throws Exception {
		invocation.push(actor);
		return this;
	}

	@Override
	public Type toType() {
		return invocation.header.toType();
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

	@Override
	public TypeDescriptor toTypeDescriptor() {
		return invocation.toTypeDescriptor();
	}
}
