package com.xarql.kdl.calculable;

import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.LinedMethodVisitor;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;

public class MethodCall extends DefaultResolvable {
    public final JavaMethodDef jmd;

    public MethodCall(JavaMethodDef javaMethodDef) {
        jmd = javaMethodDef;
    }

    @Override
    /**
     * Executes the method and pushes the return value on to the stack
     */
    public void push(LinedMethodVisitor lmv) throws Exception {

    }

    @Override
    public InternalName toInternalName() {
        return jmd.returnValue.returnType.toInternalName();
    }

    @Override
    public InternalObjectName toInternalObjectName() {
        return jmd.returnValue.returnType.toInternalObjectName();
    }

    @Override
    public boolean isBaseType() {
        return jmd.returnValue.returnType.isBaseType();
    }

    @Override
    public BaseType toBaseType() {
        return jmd.returnValue.returnType.toBaseType();
    }
}
