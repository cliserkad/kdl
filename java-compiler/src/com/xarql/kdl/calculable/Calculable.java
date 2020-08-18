package com.xarql.kdl.calculable;

import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

/**
 * Represents anything that can be calculated after its arguments are on the stack
 */
public interface Calculable extends ToName {
    /**
     * Push contents to stack and execute instructions to calculate result
     * @return expected result type
     */
    public ToName calc(final MethodVisitor visitor) throws Exception;
}
