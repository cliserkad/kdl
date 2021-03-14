package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;

import static com.xarql.kdl.ExternalMethodRouter.ERROR_MTD;
import static com.xarql.kdl.ExternalMethodRouter.PRINT_MTD;
import static com.xarql.kdl.names.CommonText.KEYWORD_FALSE;

public class Assertion extends Conditional {

	public Assertion(Condition condition, Actor actor) {
		super(condition, actor);
	}

	@Override
	public void defineOnTrue(kdl.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
		if(actor.unit.hasConstant("ASSERTION_PASS")) {
			actor.unit.getConstant("ASSERTION_PASS").push(actor);
			PRINT_MTD.withOwner(actor.unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC).invoke(actor);
		}
		actor.visitJumpInsn(GOTO, labelSet.exit); // jump over the false instructions
	}

	@Override
	public void defineOnFalse(kdl.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onFalse);
		// push the text of the assertion condition
		String msg;
		if(ctx.assertion().condition().getText().equals(KEYWORD_FALSE))
			msg = "Failed assertion of false. Thus, this message was shown in error.";
		else
			msg = "Failed assertion with condition " + ctx.assertion().condition().getText();
		new Literal<>(msg).push(actor);
		// print the text of the assertion condition to the error stream
		ERROR_MTD.withOwner(actor.unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC).invoke(actor);
	}

}
