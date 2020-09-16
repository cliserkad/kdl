package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.Details;

public class VariableDeclaration extends VariableAssignment {

	public VariableDeclaration(final com.xarql.kdl.antlr.kdl.VariableDeclarationContext ctx, final Actor actor) throws Exception {
		this(actor.unit.parseTypedVariable(ctx.typedVariable()), ctxExpression(ctx, actor), actor.unit);
	}

	public VariableDeclaration(final Details details, final Pushable pushable, CompilationUnit unit) {
		super(pushable, unit.getCurrentScope().newVariable(details.name, details.type, details.mutable));
	}

	public static Expression ctxExpression(final kdl.VariableDeclarationContext ctx, final Actor actor) throws Exception {
		if(ctx.ASSIGN() != null)
			return new Expression(ctx.expression(), actor);
		else
			return null;
	}

}
