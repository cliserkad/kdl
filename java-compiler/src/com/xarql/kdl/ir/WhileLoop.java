package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;

public class WhileLoop extends Conditional {
    public WhileLoop(Condition condition, Actor actor) {
        super(condition, actor);
    }

    @Override
    public void defineOnTrue(com.xarql.kdl.antlr.kdl.ConditionalContext ctx, Actor actor) throws Exception {
        actor.visitLabel(labelSet.onTrue);
        actor.unit.consumeBlock(ctx.r_while().block(), actor);
        actor.visitJumpInsn(GOTO, labelSet.check);
    }

}
