package com.xarql.kdl;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.calculable.Calculable;
import com.xarql.kdl.calculable.Expression;
import com.xarql.kdl.calculable.Variable;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

public class VariableDeclaration {
    Details details;
    Calculable calculable;
    Variable variable;

    public VariableDeclaration(final kdl.VariableDeclarationContext ctx, final CompilationUnit unit) throws Exception {
        this(unit.parseTypedVariable(ctx.typedVariable()), ctxExpression(ctx, unit), unit);
    }

    public VariableDeclaration(final Details details, final Calculable calculable, CompilationUnit unit) {
        if(details == null)
            throw new NullPointerException();
        this.details = details;
        this.calculable = calculable;
        variable = unit.getCurrentScope().newVariable(details.name, details.type, details.mutable);
    }

    public Variable store(final MethodVisitor visitor) throws Exception {
        if(calculable != null) {
            ToName type = calculable.calc(visitor);
            CompilationUnit.store(type, variable, visitor);
        }
        else {
            CompilationUnit.storeDefault(variable, visitor);
        }
        return variable;
    }

    public static Expression ctxExpression(final kdl.VariableDeclarationContext ctx, final CompilationUnit unit) throws Exception {
        if(ctx.ASSIGN() != null)
            return new Expression(ctx.expression(), unit);
        else
            return null;
    }
}
