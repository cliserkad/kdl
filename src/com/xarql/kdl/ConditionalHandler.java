package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class ConditionalHandler implements CommonNames, Opcodes {
	private final SourceListener owner;

	public ConditionalHandler(SourceListener owner) {
		this.owner = owner;
	}


	public void handle(kdlParser.ConditionalContext ctx, LinedMethodVisitor lmv) {
		if(ctx.r_if() != null) {

			Label trueLabel = new Label();
			Label falseLabel = new Label();
			Label endLabel = new Label();

			final kdlParser.ConditionContext cnd = ctx.r_if().condition();

			final Value a = owner.pushValue(cnd.value(0), lmv);
			// if the condition has two values
			if(cnd.value(1) != null) {
				final Comparator cmp = Comparator.match(cnd.comparator().getText());
				final Value b = owner.pushValue(cnd.value(1), lmv);

				// check type compatibility
				if(!a.toInternalName().equals(b.toInternalName()))
					SourceListener.standardHandle(new IncompatibleTypeException("The type " + a.toInternalName() + " is not compatible with " + b.toInternalName()));

				if(a.toBaseType() == BOOLEAN) {
					testBooleans(lmv, trueLabel, cmp);
				}
				else
					SourceListener.standardHandle(new UnimplementedException("Conditions are not complete"));
			}
			else if(a.isBaseType()) {
				switch(a.toBaseType()) {
					case BOOLEAN:
					case INT:
						lmv.visitJumpInsn(IFGT, trueLabel);
						break;
					case STRING:
						testStringUsability(lmv, trueLabel, falseLabel);
						break;
				}
			}
			else
				SourceListener.standardHandle(new IncompatibleTypeException("Don't know how to handle a ref type without a comparator"));

			// label and write out the instructions within the else clause
			lmv.visitLabel(falseLabel);
			if(ctx.r_if().r_else() != null)
				owner.consumeStatementSet(ctx.r_if().r_else().statementSet(), lmv);
			lmv.visitJumpInsn(GOTO, endLabel); // jump over the if instructions

			// label and write out instructions within the if clause
			lmv.visitLabel(trueLabel);
			owner.consumeStatementSet(ctx.r_if().statementSet(), lmv);
			// no need to jump to the end since we're already there

			lmv.visitLabel(endLabel);
		}
	}

	/**
	 * Writes the instructions needed to determine that the String on the stack is not null and not empty
	 * @param lmv Visitor to write with
	 * @param tl  Label for true clause
	 * @param fl  Label for false clause
	 */
	private static void testStringUsability(LinedMethodVisitor lmv, Label tl, Label fl) {
		// Label skip = new Label();
		// lmv.visitInsn(DUP);
		// lmv.visitJumpInsn(IFNULL, skip);
		// only check if string is empty. Null test is broken
		// FIXME: fix null test on strings
		new JavaMethodDef(STRING_IN, "isEmpty", null, BOOLEAN_RV, ACC_PUBLIC + ACC_STATIC).invokeVirtual(lmv);
		lmv.visitJumpInsn(IFEQ, tl);
		// lmv.visitLabel(skip);
		// lmv.visitInsn(POP);
		lmv.visitJumpInsn(GOTO, fl);
	}

	private static void testBooleans(LinedMethodVisitor lmv, Label tl, Comparator cmp) {
		switch(cmp) {
			case EQUAL:
				lmv.visitJumpInsn(IF_ICMPEQ, tl);
				break;
			case NOT_EQUAL:
				lmv.visitJumpInsn(IF_ICMPNE, tl);
				break;
			default:
				SourceListener.standardHandle(new IncompatibleTypeException("Two booleans may not be compared with " + cmp));
		}
	}

}
