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

	private kdlParser.ConditionContext conditionContextof(kdlParser.ConditionalContext ctx) {
		if(ctx.r_if() != null)
			return ctx.r_if().condition();
		else if(ctx.r_while() != null)
			return ctx.r_while().condition();
		else if(ctx.assertion() != null)
			return ctx.assertion().condition();
		else {
			SourceListener.standardHandle(new UnimplementedException("Retrieving a conditional's condition failed"));
			return null;
		}
	}

	// route a certain condition to the conditional's flow
	private void handleSingleCondition(kdlParser.SingleConditionContext ctx, ConditionalLabelSet cls, LinedMethodVisitor lmv) {
		final Value a = owner.pushValue(ctx.value(0), lmv);
		// if the condition has two values
		if(ctx.value(1) != null) {
			final Comparator cmp = Comparator.match(ctx.comparator().getText());
			final Value b = owner.pushValue(ctx.value(1), lmv);

			if(a.valueType == ARRAY_LENGTH || b.valueType == ARRAY_LENGTH) {
				if(a.valueType == ARRAY_LENGTH) {
					if(b.content.isBaseType() && b.content.toBaseType() == INT)
						testIntegers(lmv, cls, cmp);
					else
						SourceListener.standardHandle(new IncompatibleTypeException("The length of an array can only be compared to an int."));
				}
				else {
					if(a.content.isBaseType() && a.content.toBaseType() == INT)
						testIntegers(lmv, cls, cmp);
					else
						SourceListener.standardHandle(new IncompatibleTypeException("The length of an array can only be compared to an int."));
				}
			}
			else {
				// check type compatibility
				if(!a.toInternalName().equals(b.toInternalName()))
					SourceListener.standardHandle(new IncompatibleTypeException("The type " + a.toInternalName() + " is not compatible with " + b.toInternalName()));

				if(a.toBaseType() == BOOLEAN)
					testBooleans(lmv, cls, cmp);
				else if(a.toBaseType() == INT)
					testIntegers(lmv, cls, cmp);
				else
					SourceListener.standardHandle(new UnimplementedException("Conditions are not complete"));
			}
		}
		else if(a.isBaseType()) {
			switch(a.toBaseType()) {
				case BOOLEAN:
				case INT:
					// if the value equals 0 (aka false) skip to the false instructions
					lmv.visitJumpInsn(IFEQ, cls.onFalse);
					break;
				case STRING:
					testStringUsability(lmv, cls);
					break;
			}
		}
		else
			SourceListener.standardHandle(new IncompatibleTypeException("Don't know how to handle a ref type without a comparator"));
	}

	public void handle(kdlParser.ConditionalContext ctx, LinedMethodVisitor lmv) {
		ConditionalLabelSet cls = new ConditionalLabelSet();
		lmv.visitLabel(cls.intro);

		final kdlParser.ConditionContext cnd = conditionContextof(ctx);
		for(kdlParser.SingleConditionContext sc : cnd.singleCondition()) {
			handleSingleCondition(sc, cls, lmv);
		}

		// write instructions that correspond with the conditional's desired flow
		// always write out the true flow first
		if(ctx.r_if() != null) {
			// label and write out instructions within the if clause
			lmv.visitLabel(cls.onTrue);
			owner.consumeStatementSet(ctx.r_if().statementSet(), lmv);
			lmv.visitJumpInsn(GOTO, cls.exit); // jump over the else instructions

			// label and write out the instructions within the else clause
			lmv.visitLabel(cls.onFalse);
			if(ctx.r_if().r_else() != null)
				owner.consumeStatementSet(ctx.r_if().r_else().statementSet(), lmv);
			// no need to jump to the end since we're already there
			lmv.visitLabel(cls.exit);
		}
		else if(ctx.assertion() != null) {
			// label and write out instructions for printing a constant when the assertion passes
			lmv.visitLabel(cls.onTrue);
			if(owner.owner.hasConstant("ASSERTION_PASS")) {
				owner.pushConstant("ASSERTION_PASS", lmv);
				ExternalMethodRouter.writeMethod(PRINT, lmv, null);
			}
			lmv.visitJumpInsn(GOTO, cls.exit); // jump over the false instructions

			// label and write out the instructions for when the assertion fails
			lmv.visitLabel(cls.onFalse);
			// push the text of the assertion condition
			owner.pushLiteral(new Literal("Failed assertion with condition " + ctx.assertion().condition().getText()), lmv);
			// print the text of the assertion condition to the error stream
			ExternalMethodRouter.writeMethod(ERROR, lmv, null);

			lmv.visitLabel(cls.exit);
		}
		else if(ctx.r_while() != null) {
			// label and write out the instructions for when the while loop continues
			lmv.visitLabel(cls.onTrue);
			owner.consumeStatementSet(ctx.r_while().statementSet(), lmv);
			lmv.visitJumpInsn(GOTO, cls.intro);

			// label and write out the instructions for when the while loop exits
			lmv.visitLabel(cls.onFalse);
			lmv.visitJumpInsn(GOTO, cls.exit);

			// label end
			lmv.visitLabel(cls.exit);
		}
		else
			SourceListener.standardHandle(new UnimplementedException("A type of conditional"));
	}

	/**
	 * Writes the instructions needed to determine that the String on the stack is not null and not empty.
	 * Expects that a String is at the top of the stack.
	 * @param lmv Visitor to write with
	 * @param cls Contains the label for false clause
	 */
	private static void testStringUsability(LinedMethodVisitor lmv, ConditionalLabelSet cls) {
		Label isEmpty = new Label();
		lmv.visitInsn(DUP); // duplicate string
		lmv.visitJumpInsn(IFNONNULL, isEmpty); // test against null; destroys first copy. Could jump to isEmpty
		lmv.visitInsn(POP); // remove unnecessary copy as no check will take place
		lmv.visitJumpInsn(GOTO, cls.onFalse); // skip isEmpty check

		// use isEmpty() on the second copy of the string
		lmv.visitLabel(isEmpty);
		new JavaMethodDef(STRING_IN, "isEmpty", null, BOOLEAN_RV, ACC_PUBLIC + ACC_STATIC).invokeVirtual(lmv);
		lmv.visitJumpInsn(IFNE, cls.onFalse); // if the string is empty, then skip to false clause
	}

	private static void testBooleans(LinedMethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp) {
		switch(cmp) {
			case EQUAL:
				lmv.visitJumpInsn(IF_ICMPNE, cls.onFalse);
				break;
			case NOT_EQUAL:
				lmv.visitJumpInsn(IF_ICMPEQ, cls.onFalse);
				break;
			default:
				SourceListener.standardHandle(new IncompatibleTypeException("Two booleans may not be compared with " + cmp));
		}
	}

	private static void testIntegers(LinedMethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp) {
		// do the opposite comparison in jump to false clause
		switch(cmp) {
			case EQUAL:
				lmv.visitJumpInsn(IF_ICMPNE, cls.onFalse);
				break;
			case NOT_EQUAL:
				lmv.visitJumpInsn(IF_ICMPEQ, cls.onFalse);
				break;
			case MORE_THAN:
				lmv.visitJumpInsn(IF_ICMPLE, cls.onFalse);
				break;
			case LESS_THAN:
				lmv.visitJumpInsn(IF_ICMPGE, cls.onFalse);
				break;
			case MORE_OR_EQUAL:
				lmv.visitJumpInsn(IF_ICMPLT, cls.onFalse);
				break;
			case LESS_OR_EQUAL:
				lmv.visitJumpInsn(IF_ICMPGT, cls.onFalse);
				break;
			default:
				SourceListener.standardHandle(new IncompatibleTypeException("Two ints may not be compared with " + cmp));
		}
	}

}
