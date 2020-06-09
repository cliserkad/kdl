package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.ToBaseType;

public class Literal<Type> implements ToBaseType, StringOutput, CommonNames {
	Type value;

	public Literal(Type value) {
		if(!BaseType.isBaseType(value))
			throw new IllegalArgumentException("Literal may only have Types defined in the BaseType enum, but the type was " + value.getClass());
		else
			this.value = value;
	}

	@Override
	public boolean isBaseType() {
		return true;
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.matchValue(value);
	}

	@Override
	public String stringOutput() {
		switch(toBaseType()) {
			case INT:
			case BOOLEAN:
				return value + "";
			case STRING:
				return (String) value;
			default:
				SourceListener.standardHandle(new UnimplementedException(SWITCH_BASETYPE));
				return null;
		}
	}

	@Override
	public String toString() {
		return "Literal: " + toBaseType().name() + " --> " + value;
	}
}
