package com.xarql.kdl.calculable;

import com.xarql.kdl.LinedMethodVisitor;
import com.xarql.kdl.names.*;

public class Null implements Resolvable, CommonNames {
    @Override
    public Resolvable push(LinedMethodVisitor lmv) throws Exception {
        lmv.visitInsn(ACONST_NULL);
        return this;
    }

    @Override
    public ToName calc(LinedMethodVisitor lmv) throws Exception {
        push(lmv);
        return toInternalName();
    }

    @Override
    public InternalName toInternalName() {
        return InternalName.internalName(Object.class);
    }

    @Override
    public InternalObjectName toInternalObjectName() {
        return toInternalName().toInternalObjectName();
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
