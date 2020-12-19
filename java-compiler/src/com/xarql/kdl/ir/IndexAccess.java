package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.MethodHeader;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;

import static com.xarql.kdl.names.BaseType.INT;

/**
 * Represents the access of an array's element
 */
public class IndexAccess implements Pushable, CommonText {

	public static final MethodHeader STRING_CHAR_AT = new MethodHeader(InternalName.STRING, "charAt", MethodHeader.toParamList(BaseType.INT.toInternalName()), ReturnValue.CHAR, ACC_PUBLIC);

	public final Pushable operand;
	public final Pushable index;

	public IndexAccess(final Pushable operand, final Pushable index) {
		this.operand = operand;
		this.index = index;
	}

    @Override
	public IndexAccess push(final Actor actor) throws Exception {
		operand.push(actor);
		// throw error if value within [ ] isn't an int
		if(index.toBaseType().ordinal() > INT.ordinal())
			throw new IncompatibleTypeException("The input for an array access must be an integer");
		else
			index.push(actor);

		if(operand.toInternalName().isArray()) {
			if(operand.toInternalName().isBaseType()) {
				switch(operand.toInternalName().toBaseType()) {
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
	public InternalName toInternalName() {
		if(!operand.toInternalName().isArray() && operand.toInternalName().equals(InternalName.STRING))
			return InternalName.CHAR;
		else
			return operand.toInternalName();
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
