package com.xarql.kdl.calculable;

import com.xarql.kdl.names.*;
import org.objectweb.asm.MethodVisitor;

public class Null implements Resolvable, CommonNames {
    @Override
    public Resolvable push(final MethodVisitor visitor) throws Exception {
        visitor.visitInsn(ACONST_NULL);
        return this;
    }

    @Override
    public ToName calc(final MethodVisitor visitor) throws Exception {
        push(visitor);
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