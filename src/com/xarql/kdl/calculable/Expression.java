package com.xarql.kdl.calculable;

import com.xarql.kdl.LinedMethodVisitor;
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
