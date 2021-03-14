package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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

			switch(aType) {
				case BOOLEAN:
					testBooleans(actor);
					break;
				case BYTE:
				case SHORT:
				case CHAR:
				case INT:
					testIntegers(actor);
					break;
				case STRING:
					testStrings(actor);
					break;
				default:
					throw new UnimplementedException(CommonText.SWITCH_BASETYPE);
			}
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
	 * Writes the instructions needed to determine that the String on the stack is
	 * not null and not empty. Expects that a String is at the top of the stack.
	 */
	public final void testStringUsability(final MethodVisitor visitor) {
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
	}

	public final void testStrings(final MethodVisitor visitor) throws Exception {
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
	}

	public final void testIntegers(final MethodVisitor visitor) throws Exception {
		switch(condition.cmp) {
			case EQUAL:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPEQ, labelSet.onTrue);
				else
					visitor.visitJumpInsn(IF_ICMPNE, labelSet.onFalse);
				break;
			case NOT_EQUAL:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPNE, labelSet.onTrue);
				else
					visitor.visitJumpInsn(IF_ICMPEQ, labelSet.onFalse);
				break;
			case MORE_THAN:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPGE, labelSet.onTrue);
				else
					visitor.visitJumpInsn(IF_ICMPLE, labelSet.onFalse);
				break;
			case LESS_THAN:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPLE, labelSet.onTrue);
				else
					visitor.visitJumpInsn(IF_ICMPGE, labelSet.onFalse);
				break;
			case MORE_OR_EQUAL:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPGT, labelSet.onTrue);
				else
					visitor.visitJumpInsn(IF_ICMPLT, labelSet.onFalse);
				break;
			case LESS_OR_EQUAL:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPLT, labelSet.onFalse);
				else
					visitor.visitJumpInsn(IF_ICMPGT, labelSet.onFalse);
				break;
			default:
				throw new IncompatibleTypeException("Two ints may not be compared with " + condition.cmp);
		}
	}

	public final void testBooleans(final MethodVisitor visitor) throws Exception {
		switch(condition.cmp) {
			case EQUAL:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPEQ, labelSet.onTrue);
				else
					visitor.visitJumpInsn(IF_ICMPNE, labelSet.onFalse);
				break;
			case NOT_EQUAL:
				if(condition.positive)
					visitor.visitJumpInsn(IF_ICMPNE, labelSet.onTrue);
				else
					visitor.visitJumpInsn(IF_ICMPEQ, labelSet.onFalse);
				break;
			default:
				throw new IncompatibleTypeException("Two booleans may not be compared with " + condition.cmp);
		}
	}

}
