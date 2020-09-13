package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

public class VariableAssignment extends BasePushable {
    private final Pushable pushable;
    private final Variable variable;

    public VariableAssignment(final Pushable pushable, final Variable variable) {
        this.pushable = pushable;
        this.variable = variable;
    }

    @Override
    /**
     * assigns variable to pushable
     */
    public VariableAssignment push(final MethodVisitor visitor) throws Exception {
        if(pushable != null) {
            ToName type = pushable.push(visitor);
            CompilationUnit.store(type, variable, visitor);
        }
        else {
            CompilationUnit.storeDefault(variable, visitor);
        }
        return this;
    }

    @Override
    public InternalName toInternalName() {
        return variable.type;
    }

    @Override
    public boolean isBaseType() {
        return variable.isBaseType();
    }

    @Override
    public BaseType toBaseType() {
        return variable.toBaseType();
    }
}
