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

    /**
     * Attempts to parse a Resolvable symbol
     * @param unit The CompilationUnit in which the symbol appears
     * @param val The symbol
     * @return A Resolvable whose actual type corresponds to the symbol
     * @throws UnimplementedException thrown if missing a symbol from the grammar
     */
    public static Resolvable parse(final CompilationUnit unit, final com.xarql.kdl.antlr4.kdlParser.ValueContext val) throws Exception {
        if(val.literal() != null)
            return Literal.parseLiteral(val.literal());
        else if(val.CONSTNAME() != null)
            return unit.getConstant(val.CONSTNAME().getText());
        else if(val.VARNAME() != null)
            return unit.getLocalVariable(val.VARNAME().getText());
        else if(val.arrayAccess() != null)
            return new ArrayAccess(unit.getLocalVariable(val.arrayAccess().VARNAME().getText()), parse(unit, val.arrayAccess().expression().value(0)));
        else if(val.arrayLength() != null)
            return new ArrayLength(unit.getLocalVariable(val.arrayLength().VARNAME().getText()));
        else if(val.R_NULL() != null)
            return new Null();
        else if(val.methodCall() != null)
            return new MethodCall(val.methodCall(), unit);
        else
            throw new UnimplementedException("a type of Resolvable wasn't parsed correctly\n The input text was \"" + val.getText() + "\"");
    }
}
