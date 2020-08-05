package com.xarql.kdl.calculable;

import com.xarql.kdl.LinedMethodVisitor;
import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.UnimplementedException;

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

    public static Resolvable parse(final CompilationUnit unit, final com.xarql.kdl.antlr4.kdlParser.ValueContext val) throws UnimplementedException {
        if(val.literal() != null)
            return Literal.parseLiteral(val.literal());
        else if(val.CONSTNAME() != null)
            return unit.getConstant(val.CONSTNAME().getText());
        else if(val.VARNAME() != null)
            return unit.getLocalVariable(val.VARNAME().getText());
        else if(val.arrayAccess() != null)
            return new ArrayAccess(unit.getLocalVariable(val.VARNAME().getText()), parse(unit, val.arrayAccess().expression().value(0)));
        else
            throw new UnimplementedException("a type of Resolvable wasn't parsed correctly\n The input text was \"" + val.getText() + "\"");
    }
}
