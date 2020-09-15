package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

public class VariableDeclaration extends VariableAssignment {

	public VariableDeclaration(final com.xarql.kdl.antlr.kdl.VariableDeclarationContext ctx, final CompilationUnit unit) throws Exception {
		this(unit.parseTypedVariable(ctx.typedVariable()), ctxExpression(ctx, unit), unit);
	}

	public VariableDeclaration(final Details details, final Pushable pushable, CompilationUnit unit) {
		super(pushable, unit.getCurrentScope().newVariable(details.name, details.type, details.mutable));
	}

	public static Expression ctxExpression(final kdl.VariableDeclarationContext ctx, final CompilationUnit unit) throws Exception {
		if (ctx.ASSIGN() != null)
			return new Expression(ctx.expression(), unit);
		else
			return null;
	}

}
