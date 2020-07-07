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
	private void handleSingleCondition(kdlParser.SingleConditionContext ctx, ConditionalLabelSet cls, LinedMethodVisitor lmv, boolean positive) {
		final Value a = owner.pushValue(ctx.value(0), lmv);
		// if the condition has two values
		if(ctx.value(1) != null) { // if there are two values
			final Comparator cmp = Comparator.match(ctx.comparator().getText());
			final Value b = owner.pushValue(ctx.value(1), lmv);

			if(a.valueType == ARRAY_LENGTH || b.valueType == ARRAY_LENGTH) {
				if(a.valueType == ARRAY_LENGTH) {
					if(b.content.isBaseType() && b.content.toBaseType() == INT)
						testIntegers(lmv, cls, cmp, positive);
					else
						SourceListener.standardHandle(new IncompatibleTypeException("The length of an array can only be compared to an int."));
				}
				else {
					if(a.content.isBaseType() && a.content.toBaseType() == INT)
						testIntegers(lmv, cls, cmp, positive);
					else
						SourceListener.standardHandle(new IncompatibleTypeException("The length of an array can only be compared to an int."));
				}
			}
			else {
				// check type compatibility
				if(!a.toInternalName().equals(b.toInternalName()))
					SourceListener.standardHandle(new IncompatibleTypeException("The type " + a.toInternalName() + " is not compatible with " + b.toInternalName()));

				if(a.toBaseType() == BOOLEAN)
					testBooleans(lmv, cls, cmp, positive);
				else if(a.toBaseType() == INT)
					testIntegers(lmv, cls, cmp, positive);
				else
					SourceListener.standardHandle(new UnimplementedException("Conditions are not complete"));
			}
		}
		else if(a.isBaseType()) {
			switch(a.toBaseType()) {
				case BOOLEAN:
				case INT:
					if(positive)
						lmv.visitJumpInsn(IFNE, cls.onTrue);
					else
						lmv.visitJumpInsn(IFEQ, cls.onFalse);
					break;
				case STRING:
					testStringUsability(lmv, cls, positive);
					break;
			}
		}
		else
			SourceListener.standardHandle(new IncompatibleTypeException("Don't know how to handle a ref type without a comparator"));
	}

	public void handle(kdlParser.ConditionalContext ctx, LinedMethodVisitor lmv) {
		final ConditionalLabelSet cls = new ConditionalLabelSet();
		lmv.visitLabel(cls.intro);

		final kdlParser.ConditionContext cnd = conditionContextof(ctx);
		final boolean checkPositive;
		if(cnd.appender() != null)
			checkPositive = cnd.appender().AND() == null;
		else
			checkPositive = false;
		for(kdlParser.SingleConditionContext sc : cnd.singleCondition())
			handleSingleCondition(sc, cls, lmv, checkPositive);

		// if the check is positive, then we should jump to the false clause when no previous jump has been triggered
		if(checkPositive)
			lmv.visitJumpInsn(GOTO, cls.onFalse);
		lmv.visitLabel(cls.onTrue);

		// write instructions that correspond with the conditional's desired flow
		// always write out the true flow first
		if(ctx.r_if() != null) {
			// label and write out instructions within the if clause
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
			if(owner.owner.hasConstant("ASSERTION_PASS")) {
				owner.pushConstant("ASSERTION_PASS", lmv);
				ExternalMethodRouter.writeMethod(PRINT, lmv, null);
			}
			lmv.visitJumpInsn(GOTO, cls.exit); // jump over the false instructions

			// label and write out the instructions for when the assertion fails
			lmv.visitLabel(cls.onFalse);
			// push the text of the assertion condition
			String msg;
			if(ctx.assertion().condition().getText().equals(KEYWORD_FALSE))
				msg = "Failed assertion of false. Thus, this message was shown in error.";
			else
				msg = "Failed assertion with condition " + ctx.assertion().condition().getText();
			owner.pushLiteral(new Literal(msg), lmv);
			// print the text of the assertion condition to the error stream
			ExternalMethodRouter.writeMethod(ERROR, lmv, null);

			lmv.visitLabel(cls.exit);
		}
		else if(ctx.r_while() != null) {
			// label and write out the instructions for when the while loop continues
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
	private static void testStringUsability(LinedMethodVisitor lmv, ConditionalLabelSet cls, boolean positive) {
		Label isEmpty = new Label();
		lmv.visitInsn(DUP); // duplicate string
		lmv.visitJumpInsn(IFNONNULL, isEmpty); // test against null; destroys first copy. Could jump to isEmpty
		lmv.visitInsn(POP); // remove unnecessary copy as no check will take place
		lmv.visitJumpInsn(GOTO, cls.onFalse); // skip isEmpty check

		// use isEmpty() on the second copy of the string
		lmv.visitLabel(isEmpty);
		new JavaMethodDef(STRING_IN, "isEmpty", null, BOOLEAN_RV, ACC_PUBLIC + ACC_STATIC).invokeVirtual(lmv);

		// negative vs positive jump
		if(positive)
			lmv.visitJumpInsn(IFEQ, cls.onTrue); // if the string is not empty, then skip to true clause
		else
			lmv.visitJumpInsn(IFNE, cls.onFalse); // if the string is empty, then skip to false clause
	}

	private static void testBooleans(LinedMethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp, boolean positive) {
		switch(cmp) {
			case EQUAL:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPEQ, cls.onTrue);
				else
					lmv.visitJumpInsn(IF_ICMPNE, cls.onFalse);
				break;
			case NOT_EQUAL:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPNE, cls.onTrue);
				else
					lmv.visitJumpInsn(IF_ICMPEQ, cls.onFalse);
				break;
			default:
				SourceListener.standardHandle(new IncompatibleTypeException("Two booleans may not be compared with " + cmp));
		}
	}

	private static void testIntegers(LinedMethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp, boolean positive) {
		switch(cmp) {
			case EQUAL:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPEQ, cls.onTrue);
				else
					lmv.visitJumpInsn(IF_ICMPNE, cls.onFalse);
				break;
			case NOT_EQUAL:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPNE, cls.onTrue);
				else
					lmv.visitJumpInsn(IF_ICMPEQ, cls.onFalse);
				break;
			case MORE_THAN:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPGE, cls.onTrue);
				else
					lmv.visitJumpInsn(IF_ICMPLE, cls.onFalse);
				break;
			case LESS_THAN:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPLE, cls.onTrue);
				else
					lmv.visitJumpInsn(IF_ICMPGE, cls.onFalse);
				break;
			case MORE_OR_EQUAL:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPGT, cls.onTrue);
				else
					lmv.visitJumpInsn(IF_ICMPLT, cls.onFalse);
				break;
			case LESS_OR_EQUAL:
				if(positive)
					lmv.visitJumpInsn(IF_ICMPLT, cls.onFalse);
				else
					lmv.visitJumpInsn(IF_ICMPGT, cls.onFalse);
				break;
			default:
				SourceListener.standardHandle(new IncompatibleTypeException("Two ints may not be compared with " + cmp));
		}
	}

}
