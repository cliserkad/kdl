package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;

public class ForLoop extends Conditional {
    public final Variable iterator;

    /**
     * Sets up the condition, gives it to super, then extracts fields from super's condition.
     */
    public ForLoop(final kdl.For_loopContext forLoop, final Actor actor) throws Exception {
        super(setUpForLoop(forLoop, actor), actor);
        iterator = (Variable) condition.a;
    }

    public static Condition setUpForLoop(final com.xarql.kdl.antlr.kdl.For_loopContext forLoop, final Actor actor) throws Exception {
        final Range r = new Range(forLoop.range(), actor.unit);
        final Details incrementDetails = new Details(forLoop.VARNAME().getText(), InternalName.INT, true);
        // set iterator to min - 1
        final Variable increment = new VariableDeclaration(incrementDetails, r.min, actor.unit).push(actor).variable;
        return new Condition(increment, r.max, Comparator.LESS_THAN);
    }

    @Override
    public void defineOnTrue(final kdl.ConditionalContext ctx, final Actor actor) throws Exception {
        actor.visitLabel(labelSet.onTrue);
        actor.unit.consumeBlock(ctx.for_loop().block(), actor);
        new VariableAssignment(new Expression(iterator, new Literal<>(1), Operator.PLUS), iterator).push(actor);
        actor.visitJumpInsn(GOTO, labelSet.check);
    }

}
