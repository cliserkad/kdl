package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.BaseType.INT;

/** Represents the access of an array's element */
public class IndexAccess extends BasePushable implements CommonText {

	public static final JavaMethodDef STRING_CHAR_AT = new JavaMethodDef(InternalName.STRING, "charAt", list(BaseType.INT.toInternalName()), ReturnValue.CHAR, ACC_PUBLIC);

	public final Variable variable;
	public final Pushable index;

	public IndexAccess(final Variable variable, final Pushable index) {
		this.variable = variable;
		this.index = index;
	}

	@Override
	public IndexAccess push(final Actor visitor) throws Exception {
		visitor.visitVarInsn(ALOAD, variable.localIndex);
		// throw error if value within [ ] isn't an int
		if(index.toBaseType().ordinal() > INT.ordinal())
			throw new IncompatibleTypeException("The input for an array access must be an integer");
		else
			index.push(visitor);

		if(variable.isArray()) {
			if(variable.type.isBaseType()) {
				switch(variable.type.toBaseType()) {
					case INT:
					case BOOLEAN:
						visitor.visitInsn(IALOAD);
						break;
					case STRING:
						visitor.visitInsn(AALOAD);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			} else
				visitor.visitInsn(AALOAD);
		} else if(variable.toBaseType() == BaseType.STRING)
			STRING_CHAR_AT.invoke(visitor);
		else
			throw new IllegalArgumentException(variable + " is not an array nor a string");
		return this;
	}

	@Override
	public InternalName toInternalName() {
		if(!variable.isArray() && variable.toInternalName().equals(InternalName.STRING))
			return InternalName.CHAR;
		else
			return variable.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return variable.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return variable.toBaseType();
	}

	@Override
	public String toString() {
		return "ArrayAccess --> {\n\t" + variable + "\n\t" + index + "\n}";
	}

}
