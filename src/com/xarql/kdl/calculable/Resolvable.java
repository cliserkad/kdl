package com.xarql.kdl.calculable;

import com.xarql.kdl.LinedMethodVisitor;
import com.xarql.kdl.SourceListener;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr4.kdlParser;

/**
 * Represents anything that may be pushed on to the JVM stack.
 * Resolvables are a type of Calculable that do not require any
 * instructions to be executed after pushing. After the push method
 * is invoked, only 1 value should be added to the stack.
 */
public interface Resolvable extends Calculable {
    /**
     * Push this resolvable on to the JVM stack
     */
    public void push(LinedMethodVisitor lmv) throws Exception;

    public static Resolvable parse(final SourceListener src, final kdlParser.ValueContext val) throws UnimplementedException {
        if(val.literal() != null)
            return Literal.parseLiteral(val.literal());
        else if(val.CONSTNAME() != null)
            return src.owner.getConstant(val.CONSTNAME().getText());
        else if(val.VARNAME() != null)
            return src.owner.getLocalVariable(val.VARNAME().getText());
        else
            throw new UnimplementedException("a type of Resolvable wasn't parsed correctly\n The input text was \"" + val.getText() + "\"");
    }
}
