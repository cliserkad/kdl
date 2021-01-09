package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.Actor;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.MethodHeader;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.TypeDescriptor;
import com.xarql.kdl.names.ReturnValue;

import static com.xarql.kdl.names.BaseType.INT;

/**
 * Represents the access of an array's element
 */
public class IndexAccess implements Pushable, CommonText {

	public static final MethodHeader STRING_CHAR_AT = new MethodHeader(TypeDescriptor.STRING, "charAt", MethodHeader.toParamList(BaseType.INT.toType()), ReturnValue.CHAR, ACC_PUBLIC);

	public final Type operand;
	public final Pushable index;

	public IndexAccess(final Type operand, final Pushable index) {
		this.operand = operand;
		this.index = index;
	}

	public IndexAccess(final Type operand, kdl.IndexAccessContext ctx, Actor actor) throws Exception {
		this.operand = operand;
		index = new Expression(operand, ctx.expression(), actor);
	}

    @Override
	public IndexAccess push(final Actor actor) throws Exception {
		// throw error if value within [ ] isn't an int
		if(index.toBaseType().ordinal() > INT.ordinal())
			throw new IncompatibleTypeException("The input for an array access must be an integer");
		else
			index.push(actor);

		if(operand.toType().isArray()) {
			if(operand.isBaseType()) {
				switch(operand.toBaseType()) {
					case INT:
					case BOOLEAN:
						actor.visitInsn(IALOAD);
						break;
					case STRING:
						actor.visitInsn(AALOAD);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			} else
				actor.visitInsn(AALOAD);
		} else if(operand.toBaseType() == BaseType.STRING)
			STRING_CHAR_AT.push(actor);
		else
			throw new IllegalArgumentException(operand + " is not an array nor a string");
		return this;
	}

	@Override
	public Type toType() {
		if(!operand.toType().isArray() && operand.toType().equals(TypeDescriptor.STRING))
			return TypeDescriptor.CHAR;
		else
			return operand.toType();
	}

	@Override
	public boolean isBaseType() {
		return operand.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return operand.toBaseType();
	}

	@Override
	public String toString() {
		return "IndexAccess --> {\n\t" + operand + "\n\t" + index + "\n}";
	}

}
