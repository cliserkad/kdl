package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.ToBaseType;

public class Value implements ToBaseType {
	public final ValueType  valueType;
	public final ToBaseType value;

	public Value(ValueType valueType, ToBaseType value) {
		if(!valueType.rep.equals(value.getClass()))
			throw new IllegalArgumentException("The ValueType " + valueType + " doesn't represent " + value.getClass());
		else {
			this.valueType = valueType;
			this.value = value;
		}
	}

	@Override
	public boolean isBaseType() {
		return value.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return value.toBaseType();
	}

	@Override
	public String toString() {
		return "Value: " + valueType + " --> " + value;
	}
}
