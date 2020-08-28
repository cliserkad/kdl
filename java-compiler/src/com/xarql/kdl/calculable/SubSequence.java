package com.xarql.kdl.calculable;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.xarql.kdl.BestList.list;

public class SubSequence implements Resolvable {
    JavaMethodDef SUB_STRING = new JavaMethodDef(InternalName.STRING, "substring", list(InternalName.INT, InternalName.INT), ReturnValue.STRING, Opcodes.ACC_PUBLIC);

    public final Variable variable;
    public final Range    range;

    public SubSequence(final kdl.SubSequenceContext ctx, final CompilationUnit unit) throws Exception {
        this(unit.getLocalVariable(ctx.VARNAME().getText()), new Range(ctx.range(), unit));
    }

    public SubSequence(final Variable variable, final Range range) {
        this.variable = variable;
        this.range = range;
    }

    @Override
    public Resolvable push(MethodVisitor visitor) throws Exception {
        calc(visitor);
        return this;
    }

    @Override
    public ToName calc(MethodVisitor visitor) throws Exception {
        if(!variable.isArray() && variable.toBaseType() == BaseType.STRING) {
            variable.push(visitor);
            range.min.calc(visitor);
            range.max.calc(visitor);
            SUB_STRING.invoke(visitor);
        }
        else {
            throw new UnimplementedException("Subsequence only implemented for strings");
        }
        return InternalName.STRING;
    }

    @Override
    public InternalName toInternalName() {
        return variable.toInternalName();
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
