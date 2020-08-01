package com.xarql.kdl;

import com.xarql.kdl.names.ToName;
import com.xarql.kdl.antlr4.kdlParser;

/**
 * An interface which represents anything that may be pushed on to the JVM stack
 */
public interface Resolvable extends ToName {
    /**
     * Push this resolvable on to the JVM stack
     */
    public void push(LinedMethodVisitor lmv) throws Exception;

    public static Resolvable parse(final SourceListener src, final kdlParser.ValueContext val) {
        if(val.literal() != null)
            return Literal.parseLiteral(val.literal());
        else if(val.CONSTNAME() != null)
            return src.owner.getConstant(val.CONSTNAME().getText());
        else
            return null;
    }
}
