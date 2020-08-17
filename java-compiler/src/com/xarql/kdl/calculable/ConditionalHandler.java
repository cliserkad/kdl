package com.xarql.kdl.calculable;

import com.xarql.kdl.*;
import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.ReturnValue;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.Label;

import static com.xarql.kdl.ExternalMethodRouter.ERROR_MTD;
import static com.xarql.kdl.ExternalMethodRouter.PRINT_MTD;

public class ConditionalHandler implements CommonNames {
	private final CompilationUnit owner;

	public ConditionalHandler(CompilationUnit owner) {
		this.owner = owner;
	}

	private kdl.ConditionContext conditionContextof(kdl.ConditionalContext ctx) throws UnimplementedException {
		if(ctx.r_if() != null)
			return ctx.r_if().condition();
		else if(ctx.r_while() != null)
			return ctx.r_while().condition();
		else if(ctx.assertion() != null)
			return ctx.assertion().condition();
		else
			throw new UnimplementedException("Retrieving a conditional's condition failed");
	}

	// route a certain condition to the conditional's flow
	private void handleSingleCondition(kdl.SingleConditionContext ctx, ConditionalLabelSet cls, LinedMethodVisitor lmv, boolean positive) throws Exception {
		ToName xpr1 = ExpressionHandler.compute(new Expression(ctx.expression(0), owner), lmv);
		final BaseType aType = xpr1.toBaseType();
		// if the condition has two values
		if(ctx.expression(1) != null) { // if there are two values
			final Comparator cmp = Comparator.match(ctx.comparator().getText());

			ToName xpr2 = ExpressionHandler.compute(new Expression(ctx.expression(1), owner), lmv);
			final BaseType bType = xpr2.toBaseType();

			// check type compatibility
			if(aType != bType)
				throw new IncompatibleTypeException("The type " + aType + " is not compatible with " + bType);

			if(aType == BOOLEAN)
				testBooleans(lmv, cls, cmp, positive);
			else if(aType == INT)
				testIntegers(lmv, cls, cmp, positive);
			else
				throw new UnimplementedException("Conditions are not complete");
		}
		else {
			switch(aType) {
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
	}

	public void handle(kdl.ConditionalContext ctx, LinedMethodVisitor lmv) throws Exception {
		final ConditionalLabelSet cls = new ConditionalLabelSet();
		lmv.visitLabel(cls.intro);

		final kdl.ConditionContext cnd = conditionContextof(ctx);
		final boolean checkPositive;
		if(cnd.appender() != null)
			checkPositive = cnd.appender().AND() == null;
		else
			checkPositive = false;
		for(kdl.SingleConditionContext sc : cnd.singleCondition())
			handleSingleCondition(sc, cls, lmv, checkPositive);

		// if the check is positive, then we should jump to the false clause when no previous jump has been triggered
		if(checkPositive)
			lmv.visitJumpInsn(GOTO, cls.onFalse);
		lmv.visitLabel(cls.onTrue);

		// write instructions that correspond with the conditional's desired flow
		// always write out the true flow first
		if(ctx.r_if() != null) {
			// label and write out instructions within the if clause
			owner.consumeBlock(ctx.r_if().block(), lmv);
			lmv.visitJumpInsn(GOTO, cls.exit); // jump over the else instructions

			// label and write out the instructions within the else clause
			lmv.visitLabel(cls.onFalse);
			if(ctx.r_if().r_else() != null) {
				if(ctx.r_if().r_else().block() != null)
					owner.consumeBlock(ctx.r_if().r_else().block(), lmv);
				else
					owner.consumeStatement(ctx.r_if().r_else().statement(), lmv);
			}
			// no need to jump to the end since we're already there
			lmv.visitLabel(cls.exit);
		}
		else if(ctx.assertion() != null) {
			// label and write out instructions for printing a constant when the assertion passes
			if(owner.hasConstant("ASSERTION_PASS")) {
				owner.getConstant("ASSERTION_PASS").push(lmv);
				PRINT_MTD.withOwner(owner.getClazz()).invokeStatic(lmv);
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
			new Literal<String>(msg).push(lmv);
			// print the text of the assertion condition to the error stream
			ERROR_MTD.withOwner(owner.getClazz()).invokeStatic(lmv);

			lmv.visitLabel(cls.exit);
		}
		else if(ctx.r_while() != null) {
			// label and write out the instructions for when the while loop continues
			owner.consumeBlock(ctx.r_while().block(), lmv);
			lmv.visitJumpInsn(GOTO, cls.intro);

			// label and write out the instructions for when the while loop exits
			lmv.visitLabel(cls.onFalse);
			lmv.visitJumpInsn(GOTO, cls.exit);

			// label end
			lmv.visitLabel(cls.exit);
		}
		else
			throw new UnimplementedException("A type of conditional");
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
		new JavaMethodDef(STRING_IN, "isEmpty", null, ReturnValue.BOOLEAN_RETURN, ACC_PUBLIC + ACC_STATIC).invokeVirtual(lmv);

		// negative vs positive jump
		if(positive)
			lmv.visitJumpInsn(IFEQ, cls.onTrue); // if the string is not empty, then skip to true clause
		else
			lmv.visitJumpInsn(IFNE, cls.onFalse); // if the string is empty, then skip to false clause
	}

	private static void testBooleans(LinedMethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp, boolean positive) throws Exception {
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
				throw new IncompatibleTypeException("Two booleans may not be compared with " + cmp);
		}
	}

	private static void testIntegers(LinedMethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp, boolean positive) throws Exception {
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
				throw new IncompatibleTypeException("Two ints may not be compared with " + cmp);
		}
	}

}
