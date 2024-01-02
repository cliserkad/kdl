package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.MethodHeader;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.xarql.kdl.ir.Comparator.REF_EQUAL;
import static com.xarql.kdl.ir.Comparator.REF_NOT_EQUAL;

public abstract class Conditional implements Opcodes {

	public final Condition condition;
	public final ConditionalLabelSet labelSet;

	public Conditional(final Condition condition, final Actor actor) {
		this.condition = condition;
		this.labelSet = new ConditionalLabelSet();
	}

	public void defineOnTrue(final kdl.ConditionalContext ctx, final Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
	}

	public void defineOnFalse(final kdl.ConditionalContext ctx, final Actor actor) throws Exception {
		actor.visitLabel(labelSet.onFalse);
		actor.visitJumpInsn(GOTO, labelSet.exit);
	}

	public void defineExit(final Actor actor) {
		actor.visitLabel(labelSet.exit);
	}

	public final void checkAll(final Actor actor) throws Exception {
		actor.visitLabel(labelSet.check);
		// TODO: include multiple conditions in conditional
		check(actor);
		// if the check is positive, then we should jump to the false clause when no
		// previous jump has been triggered
		if(condition.positive)
			actor.visitJumpInsn(GOTO, labelSet.onFalse);
	}

	private void check(final Actor actor) throws Exception {
		final BaseType aType = condition.a.pushType(actor).toBaseType();
		// if the condition has two values
		if(condition.b != null) { // if there are two values
			final BaseType bType = condition.b.pushType(actor).toBaseType();

			// check type compatibility
			if(!aType.compatibleNoDirection(bType))
				throw new IncompatibleTypeException("The type " + aType + " is not compatible with " + bType);

			// this boolean is never used. it only exists to trick the compiler in to doing static analysis
			final boolean executeSwitch = switch(aType) {
				case BOOLEAN, BYTE, SHORT, CHAR, INT -> testIntegers(actor);
				case STRING -> testStrings(actor);
				case FLOAT -> testFloats(actor);
				case LONG -> {
					if(bType.isIntInternally())
						actor.visitInsn(I2L);
					yield testLongs(actor);
				}
				case DOUBLE -> {
					if(bType == BaseType.FLOAT)
						actor.visitInsn(F2D);
					yield testDoubles(actor);
				}
			};
		} else {
			switch(aType) {
				case BOOLEAN:
				case INT:
					if(condition.positive)
						actor.visitJumpInsn(IFNE, labelSet.onTrue);
					else
						actor.visitJumpInsn(IFEQ, labelSet.onFalse);
					break;
				case STRING:
					testStringUsability(actor);
					break;
			}
		}
	}

	/**
	 * Writes the instructions needed to determine that the String on the stack is not null and not empty. Expects that a String is at the top of the stack.
	 */
	public final boolean testStringUsability(final MethodVisitor visitor) {
		Label isEmpty = new Label();
		visitor.visitInsn(DUP); // duplicate string
		visitor.visitJumpInsn(IFNONNULL, isEmpty); // test against null; destroys first copy. Could jump to isEmpty
		visitor.visitInsn(POP); // remove unnecessary copy as no check will take place
		visitor.visitJumpInsn(GOTO, labelSet.onFalse); // skip isEmpty check

		// use isEmpty() on the second copy of the string
		visitor.visitLabel(isEmpty);
		new MethodHeader(InternalName.STRING, "isEmpty", null, ReturnValue.BOOLEAN, ACC_PUBLIC + ACC_STATIC).invoke(visitor);

		// negative vs positive jump
		if(condition.positive)
			visitor.visitJumpInsn(IFEQ, labelSet.onTrue); // if the string is not empty, then skip to true clause
		else
			visitor.visitJumpInsn(IFNE, labelSet.onFalse); // if the string is empty, then skip to false clause

		return true;
	}

	public final boolean testStrings(final MethodVisitor visitor) throws Exception {
		switch(condition.cmp) {
			case EQUAL:
				MethodHeader.EQUALS.withOwner(InternalName.STRING).invoke(visitor);
				break;
			default:
				throw new UnimplementedException("Only == has been implemented for strings");
		}

		// negative vs positive jump
		if(condition.positive)
			visitor.visitJumpInsn(IFNE, labelSet.onTrue); // if the strings are not equal, then skip to true clause
		else
			visitor.visitJumpInsn(IFEQ, labelSet.onFalse); // if the string are equal, then skip to false clause

		return true;
	}

	public final boolean testIntegers(final MethodVisitor visitor) throws Exception {
		final int jumpInstruction = switch(condition.cmp) {
			case EQUAL -> IF_ICMPEQ;
			case NOT_EQUAL -> IF_ICMPNE;
			case LESS_THAN -> IF_ICMPLT;
			case MORE_OR_EQUAL -> IF_ICMPGE;
			case MORE_THAN -> IF_ICMPGT;
			case LESS_OR_EQUAL -> IF_ICMPLE;
			case REF_EQUAL, REF_NOT_EQUAL -> throw new IllegalArgumentException("Cannot use REF_EQUAL or REF_NOT_EQUAL with integers");
		};
		if(condition.positive) {
			// use normal instruction, jump to true
			visitor.visitJumpInsn(jumpInstruction, labelSet.onTrue);
		} else {
			// use inverse instruction, jump to false
			final int inverseJumpInstruction;
			if(jumpInstruction % 2 == 0)
				inverseJumpInstruction = jumpInstruction - 1;
			else
				inverseJumpInstruction = jumpInstruction + 1;
			visitor.visitJumpInsn(inverseJumpInstruction, labelSet.onFalse);
		}
		return true;
	}

	public final boolean testIntAgainst0(final MethodVisitor visitor) throws Exception {
		final int jumpInstruction = switch(condition.cmp) {
			case EQUAL -> IFEQ;
			case NOT_EQUAL -> IFNE;
			case LESS_THAN -> IFLT;
			case MORE_OR_EQUAL -> IFGE;
			case MORE_THAN -> IFGT;
			case LESS_OR_EQUAL -> IFLE;
			case REF_EQUAL, REF_NOT_EQUAL -> throw new IllegalArgumentException("Cannot use REF_EQUAL or REF_NOT_EQUAL with floats");
		};
		if(condition.positive) {
			// use normal instruction, jump to true
			visitor.visitJumpInsn(jumpInstruction, labelSet.onTrue);
		} else {
			// use inverse instruction, jump to false
			final int inverseJumpInstruction;
			if(jumpInstruction % 2 == 0)
				inverseJumpInstruction = jumpInstruction - 1;
			else
				inverseJumpInstruction = jumpInstruction + 1;
			visitor.visitJumpInsn(inverseJumpInstruction, labelSet.onFalse);
		}
		return true;
	}

	public final boolean testLongs(final MethodVisitor visitor) throws Exception {
		// Testing longs requires us to first do a comparison test and then use the resulting int to jump
		if(condition.cmp == REF_EQUAL || condition.cmp == REF_NOT_EQUAL)
			throw new IllegalArgumentException("Cannot use REF_EQUAL or REF_NOT_EQUAL with longs");
		else
			visitor.visitInsn(LCMP); // all other comparisons are supported by LCMP

		// compare the int result of the long comparison to 0
		return testIntAgainst0(visitor);
	}

	public final boolean testDoubles(final MethodVisitor visitor) throws Exception {
		// Testing doubles requires us to first do a comparison test and then use the resulting int to jump
		final int doubleCompareInstruction = switch(condition.cmp) {
			// comparison should fail if either value is NaN
			case MORE_THAN, MORE_OR_EQUAL, EQUAL, NOT_EQUAL -> DCMPL; // result will represent lesser (-1) if NaN is encountered
			case LESS_THAN, LESS_OR_EQUAL -> DCMPG; // result will represent greater (1) if NaN is encountered
			case REF_EQUAL, REF_NOT_EQUAL -> throw new IllegalArgumentException("Cannot use REF_EQUAL or REF_NOT_EQUAL with doubles");
		};
		visitor.visitInsn(doubleCompareInstruction);

		// compare the int result of the double comparison to 0
		return testIntAgainst0(visitor);
	}

	public final boolean testFloats(final MethodVisitor visitor) throws Exception {
		// Testing floats requires us to first do a comparison test and then use the resulting int to jump
		final int floatCompareInstruction = switch(condition.cmp) {
			// comparison should fail if either value is NaN
			case MORE_THAN, MORE_OR_EQUAL, EQUAL, NOT_EQUAL -> FCMPL; // result will represent lesser (-1) if NaN is encountered
			case LESS_THAN, LESS_OR_EQUAL -> FCMPG; // result will represent greater (1) if NaN is encountered
			case REF_EQUAL, REF_NOT_EQUAL -> throw new IllegalArgumentException("Cannot use REF_EQUAL or REF_NOT_EQUAL with floats");
		};
		visitor.visitInsn(floatCompareInstruction);

		// compare the int result of the float comparison to 0
		return testIntAgainst0(visitor);
	}

}
