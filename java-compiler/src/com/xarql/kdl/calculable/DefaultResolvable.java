package com.xarql.kdl.calculable;

import org.objectweb.asm.MethodVisitor;

public abstract class DefaultResolvable implements Resolvable {

    @Override
    public Resolvable calc(final MethodVisitor visitor) throws Exception {
        push(visitor);
        return this;
    }

    public abstract Resolvable push(MethodVisitor visitor) throws Exception;
}
