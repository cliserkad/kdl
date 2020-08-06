package com.xarql.kdl.calculable;

import com.xarql.kdl.LinedMethodVisitor;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;
import org.objectweb.asm.Opcodes;

/**
 * Represents the access of an array's length.
 */
public class ArrayLength extends DefaultResolvable implements CommonNames {
    public final Variable array;

    public ArrayLength(final Variable array) {
        this.array = array;
    }

    /**
     * Pushes an int to the stack that is equal to the array's length
     * @param lmv any LinedMethodVisitor
     * @throws Exception unused
     */
    @Override
    public void push(LinedMethodVisitor lmv) throws Exception {
        array.push(lmv);
        lmv.visitInsn(ARRAYLENGTH);
    }

    /**
     * Forward to underlying array
     * @return array.toInternalName();
     */
    @Override
    public InternalName toInternalName() {
        return INT_IN;
    }

    /**
     * Forward to underlying array
     * @return array.toInternalObjectName();
     */
    @Override
    public InternalObjectName toInternalObjectName() {
        return INT_IN.toInternalObjectName();
    }

    /**
     * Forward to underlying array
     * @return array.isBaseType();
     */
    @Override
    public boolean isBaseType() {
        return INT_IN.isBaseType();
    }

    /**
     * Forward to underlying array
     * @return array.toBaseType();
     */
    @Override
    public BaseType toBaseType() {
        return INT_IN.toBaseType();
    }
}
