package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

public class Expression extends BasePushable {
	Pushable a;
	Pushable b;
	Operator opr;

	public Expression(Pushable a, Pushable b, Operator opr) {
		if(!a.isBaseType() || !b.isBaseType())
			throw new IllegalArgumentException("Expressions may only contain BaseTypes");
		this.a = a;
		this.b = b;
		this.opr = opr;
	}

	public Expression(kdl.ExpressionContext ctx, CompilationUnit unit) throws Exception {
		this.a = Pushable.parse(unit, ctx.value());
		if(ctx.expression() != null)
			this.b = new Expression(ctx.expression(), unit);
		else
			this.b = null;
		if(ctx.operator() != null)
			opr = Operator.match(ctx.operator().getText());
		else
			opr = null;
		if(opr != null && b == null) {
			throw new IllegalStateException("Expressions must have a right side if they have an operator");
		}
	}

	public boolean isSingleValue() {
		return a != null && b == null && opr == null;
	}

	@Override
	public Pushable push(MethodVisitor visitor) throws Exception {
		ExpressionHandler.compute(this, visitor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return a.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return a.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return a.toBaseType();
	}
}
