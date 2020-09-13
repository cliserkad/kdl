package com.xarql.kdl.ir;

import com.xarql.kdl.BestList;
import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

public class MethodCall extends BasePushable implements CommonText {
	public final  JavaMethodDef      method;
	public final  Pushable           source;
	private final BestList<Pushable> arguments;

	public MethodCall(kdl.MethodCallContext ctx, CompilationUnit unit) throws Exception {
		// parse methodCall alone
		final String methodName = ctx.VARNAME(ctx.VARNAME().size() - 1).getText();
		arguments = parseArguments(ctx.parameterSet(), unit);
		final BestList<InternalName> params = new BestList<>();
		for(Pushable arg : arguments)
			params.add(arg.toInternalName());

		// determine which class owns the method being called
		final InternalName owner;
		int accessModifier = 0;
		if(ctx.CLASSNAME() != null) {
			owner = unit.resolveAgainstImports(ctx.CLASSNAME().getText());
			accessModifier += ACC_STATIC;
			source = null;
		}
		else if(ctx.VARNAME().size() > 1) {
			source = unit.getLocalVariable(ctx.VARNAME(0).getText());
			owner = source.toInternalName();
		}
		else {
			owner = new InternalName(unit.getClazz());
			accessModifier += ACC_STATIC;
			source = null;
		}

		JavaMethodDef known = new JavaMethodDef(owner, methodName, params, null, ACC_PUBLIC + accessModifier);
		method = known.resolve(unit);
	}

	public MethodCall(kdl.MethodCallStatementContext ctx, CompilationUnit unit) throws Exception {
		this(ctx.methodCall(), unit);
	}

	public static BestList<Pushable> parseArguments(kdl.ParameterSetContext ctx, CompilationUnit unit) throws Exception {
		final BestList<Pushable> arguments = new BestList<>();
		if(ctx != null && ctx.expression().size() > 0) {
			for(kdl.ExpressionContext xpr : ctx.expression()) {
				Expression xpr1 = new Expression(xpr, unit);
				arguments.add(xpr1);
			}
		}
		return arguments;
	}

	@Override
	public MethodCall push(final MethodVisitor visitor) throws Exception {
		if(source != null)
			source.push(visitor);
		for(int i = 0; i < arguments.size(); i++) {
			ToName argType = arguments.get(i).push(visitor);
			if(method.paramTypes.get(i) == InternalName.STRING) {
				CompilationUnit.convertToString(argType.toInternalName(), visitor);
			}
		}
		method.invoke(visitor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		if(method.returnValue.returnType == null)
			return new InternalName();
		return method.returnValue.returnType.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return method.returnValue.returnType.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return method.returnValue.returnType.toBaseType();
	}

	@Override
	public String toString() {
		return "MethodCall --> {\n\tMethod --> " + method + arguments() + "\n}";
	}

	private String arguments() {
		String out = "\n\tCalculable --> {";
		for(Pushable arg : arguments)
			out += "\n\t\t" + arg.toString().replace("\n", "\n\t\t");
		out += "\n\t}";
		return out;
	}
}
