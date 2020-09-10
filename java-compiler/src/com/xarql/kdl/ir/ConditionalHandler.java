package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static com.xarql.kdl.ExternalMethodRouter.ERROR_MTD;
import static com.xarql.kdl.ExternalMethodRouter.PRINT_MTD;
import static com.xarql.kdl.names.BaseType.INT;

public class ConditionalHandler implements CommonText {
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
		else if(ctx.for_loop() != null)
			return null;
		else
			throw new UnimplementedException("Retrieving a conditional's condition failed");
	}

	// route a certain condition to the conditional's flow
	private void handleSingleCondition(kdl.SingleConditionContext ctx, ConditionalLabelSet cls, MethodVisitor visitor, boolean positive) throws Exception {
		ToName xpr1 = ExpressionHandler.compute(new Expression(ctx.expression(0), owner), visitor);
		final BaseType aType = xpr1.toBaseType();
		// if the condition has two values
		if(ctx.expression(1) != null) { // if there are two values
			final Comparator cmp = Comparator.match(ctx.comparator().getText());

			ToName xpr2 = ExpressionHandler.compute(new Expression(ctx.expression(1), owner), visitor);
			final BaseType bType = xpr2.toBaseType();

			// check type compatibility
			if(!aType.compatibleNoDirection(bType))
				throw new IncompatibleTypeException("The type " + aType + " is not compatible with " + bType);

			switch(aType) {
				case BOOLEAN:
					testBooleans(visitor, cls, cmp, positive);
					break;
				case BYTE:
				case SHORT:
				case CHAR:
				case INT:
					testIntegers(visitor, cls, cmp, positive);
					break;
				default:
					throw new UnimplementedException(SWITCH_BASETYPE);
			}
		}
		else {
			switch(aType) {
				case BOOLEAN:
				case INT:
					if(positive)
						visitor.visitJumpInsn(IFNE, cls.onTrue);
					else
						visitor.visitJumpInsn(IFEQ, cls.onFalse);
					break;
				case STRING:
					testStringUsability(visitor, cls, positive);
					break;
			}
		}
	}

	public void handle(kdl.ConditionalContext ctx, MethodVisitor visitor, CompilationUnit unit) throws Exception {
		final ConditionalLabelSet cls = new ConditionalLabelSet();


		final kdl.ConditionContext cnd = conditionContextof(ctx);
		final boolean checkPositive;
		if(cnd != null && cnd.appender() != null)
			checkPositive = cnd.appender().AND() == null;
		else
			checkPositive = false;

		if(ctx.for_loop() != null) {
			final kdl.For_loopContext loop = ctx.for_loop();

			// set up values from within for declaration
			Variable increment = unit.getCurrentScope().newVariable(ctx.for_loop().VARNAME().getText(), InternalName.INT, true);
			Range range = new Range(loop.range(), unit);
			range.min.calc(visitor);
			CompilationUnit.store(INT, increment, visitor);

			// if the check is positive, then we should jump to the false clause when no previous jump has been triggered
			if(checkPositive)
				visitor.visitJumpInsn(GOTO, cls.onFalse);
			visitor.visitLabel(cls.onTrue);

			visitor.visitLabel(cls.intro);
			// make comparison
			increment.push(visitor);
			range.max.calc(visitor);
			testIntegers(visitor, cls, Comparator.LESS_THAN, checkPositive);

			// label and write out the instructions for when the for loop continues
			owner.consumeBlock(loop.block(), visitor);
			// add increment instruction at end of block
			new Expression(increment, new Literal<>(1), Operator.PLUS).calc(visitor);
			CompilationUnit.store(INT, increment, visitor);
			visitor.visitJumpInsn(GOTO, cls.intro);

			// label and write out the instructions for when the for loop exits
			visitor.visitLabel(cls.onFalse);
			visitor.visitJumpInsn(GOTO, cls.exit);

			// label end
			visitor.visitLabel(cls.exit);
		}
		else {
			visitor.visitLabel(cls.intro);
			for(kdl.SingleConditionContext sc : cnd.singleCondition())
				handleSingleCondition(sc, cls, visitor, checkPositive);

			// if the check is positive, then we should jump to the false clause when no previous jump has been triggered
			if(checkPositive)
				visitor.visitJumpInsn(GOTO, cls.onFalse);
			visitor.visitLabel(cls.onTrue);

			// write instructions that correspond with the conditional's desired flow
			// always write out the true flow first
			if(ctx.r_if() != null) {
				// label and write out instructions within the if clause
				owner.consumeBlock(ctx.r_if().block(), visitor);
				visitor.visitJumpInsn(GOTO, cls.exit); // jump over the else instructions

				// label and write out the instructions within the else clause
				visitor.visitLabel(cls.onFalse);
				if(ctx.r_if().r_else() != null) {
					if(ctx.r_if().r_else().block() != null)
						owner.consumeBlock(ctx.r_if().r_else().block(), visitor);
					else
						throw new IllegalArgumentException("Missing block for else clause of if statement");
				}
				// no need to jump to the end since we're already there
				visitor.visitLabel(cls.exit);
			}
			else if(ctx.assertion() != null) {
				// label and write out instructions for printing a constant when the assertion passes
				if(owner.hasConstant("ASSERTION_PASS")) {
					owner.getConstant("ASSERTION_PASS").push(visitor);
					PRINT_MTD.withOwner(owner.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC).invoke(visitor);
				}
				visitor.visitJumpInsn(GOTO, cls.exit); // jump over the false instructions

				// label and write out the instructions for when the assertion fails
				visitor.visitLabel(cls.onFalse);
				// push the text of the assertion condition
				String msg;
				if(ctx.assertion().condition().getText().equals(KEYWORD_FALSE))
					msg = "Failed assertion of false. Thus, this message was shown in error.";
				else
					msg = "Failed assertion with condition " + ctx.assertion().condition().getText();
				new Literal<>(msg).push(visitor);
				// print the text of the assertion condition to the error stream
				ERROR_MTD.withOwner(owner.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC).invoke(visitor);

				visitor.visitLabel(cls.exit);
			}
			else if(ctx.r_while() != null) {
				// label and write out the instructions for when the while loop continues
				owner.consumeBlock(ctx.r_while().block(), visitor);
				visitor.visitJumpInsn(GOTO, cls.intro);

				// label and write out the instructions for when the while loop exits
				visitor.visitLabel(cls.onFalse);
				visitor.visitJumpInsn(GOTO, cls.exit);

				// label end
				visitor.visitLabel(cls.exit);
			}
			else
				throw new UnimplementedException("A type of conditional");
		}
	}

	/**
	 * Writes the instructions needed to determine that the String on the stack is not null and not empty.
	 * Expects that a String is at the top of the stack.
	 * @param lmv Visitor to write with
	 * @param cls Contains the label for false clause
	 */
	private static void testStringUsability(MethodVisitor lmv, ConditionalLabelSet cls, boolean positive) {
		Label isEmpty = new Label();
		lmv.visitInsn(DUP); // duplicate string
		lmv.visitJumpInsn(IFNONNULL, isEmpty); // test against null; destroys first copy. Could jump to isEmpty
		lmv.visitInsn(POP); // remove unnecessary copy as no check will take place
		lmv.visitJumpInsn(GOTO, cls.onFalse); // skip isEmpty check

		// use isEmpty() on the second copy of the string
		lmv.visitLabel(isEmpty);
		new JavaMethodDef(InternalName.STRING, "isEmpty", null, ReturnValue.BOOLEAN, ACC_PUBLIC + ACC_STATIC).invoke(lmv);

		// negative vs positive jump
		if(positive)
			lmv.visitJumpInsn(IFEQ, cls.onTrue); // if the string is not empty, then skip to true clause
		else
			lmv.visitJumpInsn(IFNE, cls.onFalse); // if the string is empty, then skip to false clause
	}

	private static void testBooleans(MethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp, boolean positive) throws Exception {
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

	private static void testIntegers(MethodVisitor lmv, ConditionalLabelSet cls, Comparator cmp, boolean positive) throws Exception {
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
