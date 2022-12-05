package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.BestList;
import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.MethodHeader;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.Opcodes;

public class NewObject extends BasePushable implements Opcodes {

	public final InternalName type;
	private final BestList<Pushable> arguments;

	public NewObject(final kdl.MethodCallContext ctx, Actor actor) throws Exception {
		type = actor.unit.resolveAgainstImports(ctx.addressable().ID(0).getText());
		if(type.isBaseType())
			throw new IllegalArgumentException("Can't instantiate a base type with a constructor. Use a literal instead");

		arguments = new BestList<>();
		if(ctx.parameterSet() != null && ctx.parameterSet().expression().size() > 0) {
			for(kdl.ExpressionContext xpr : ctx.parameterSet().expression()) {
				Expression xpr1 = new Expression(xpr, actor);
				arguments.add(xpr1);
			}
		}
	}

	@Override
	public NewObject push(Actor visitor) throws Exception {
		final BestList<InternalName> paramTypes = new BestList<>();
		for(Pushable arg : arguments) {
			if(arg.toInternalName() == null)
				throw new NullPointerException("The type of an argument (" + arg + ") evaluated to null");
			paramTypes.add(arg.toInternalName());
		}
		visitor.visitTypeInsn(NEW, type.nameString());
		visitor.visitInsn(DUP);
		for(int i = 0; i < arguments.size(); i++) {
			arguments.get(i).push(visitor);
			if(paramTypes.get(i) == InternalName.STRING) {
				CompilationUnit.convertToString(arguments.get(i).toInternalName(), visitor);
			}
		}
		new MethodHeader(type, MethodHeader.S_INIT, MethodHeader.toParamList(paramTypes), ReturnValue.VOID, ACC_PUBLIC).invoke(visitor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return type;
	}

	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}

}
