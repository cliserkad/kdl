package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

public class VariableDeclaration {
	Details  details;
	Pushable pushable;
	Variable variable;

	public VariableDeclaration(final kdl.VariableDeclarationContext ctx, final CompilationUnit unit) throws Exception {
		this(unit.parseTypedVariable(ctx.typedVariable()), ctxExpression(ctx, unit), unit);
	}

	public VariableDeclaration(final Details details, final Pushable calculable, CompilationUnit unit) {
		if(details == null)
			throw new NullPointerException();
		this.details = details;
		this.pushable = calculable;
		variable = unit.getCurrentScope().newVariable(details.name, details.type, details.mutable);
	}

	public Variable store(final MethodVisitor visitor) throws Exception {
		if(pushable != null) {
			ToName type = pushable.push(visitor);
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
