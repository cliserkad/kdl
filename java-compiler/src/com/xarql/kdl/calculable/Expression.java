package com.xarql.kdl.calculable;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.LinedMethodVisitor;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;
import com.xarql.kdl.names.ToName;

public class Expression implements Calculable {
    Resolvable a;
    Resolvable b;
    Operator opr;

    public Expression(Resolvable a, Resolvable b, Operator opr) {
        if(!a.isBaseType() || !b.isBaseType())
            throw new IllegalArgumentException("Expressions may only contain BaseTypes");
        this.a = a;
        this.b = b;
        this.opr = opr;
    }

    public Expression(kdl.ExpressionContext ctx, CompilationUnit unit) throws Exception {
        this.a = Resolvable.parse(unit, ctx.value(0));
        if(ctx.value(1) == null)
            this.b = null;
        else
            this.b = Resolvable.parse(unit, ctx.value(1));
        if(ctx.operator() == null)
            opr = null;
        else
            opr = Operator.match(ctx.operator().getText());
    }

    public boolean isSingleValue() {
        return a != null && b == null && opr == null;
    }

    @Override
    public ToName calc(LinedMethodVisitor lmv) throws Exception {
        return ExpressionHandler.compute(this, lmv);
    }

    @Override
    public InternalName toInternalName() {
        return null;
    }

    @Override
    public InternalObjectName toInternalObjectName() {
        return null;
    }

    @Override
    public boolean isBaseType() {
        return false;
    }

    @Override
    public BaseType toBaseType() {
        return null;
    }
}
