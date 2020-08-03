package com.xarql.kdl.calculable;

import com.xarql.kdl.LinedMethodVisitor;

public abstract class DefaultResolvable implements Resolvable{

    @Override
    public Resolvable calc(LinedMethodVisitor lmv) throws Exception {
        push(lmv);
        return this;
    }

}
