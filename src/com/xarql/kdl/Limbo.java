package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.ToBaseType;

public class Limbo implements ToBaseType {
	Object value;

	public Limbo(Object value) {
		if(!BaseType.isBaseType(value))
			SourceListener.standardHandle(new IncompatibleTypeException("A Limbo tracker needs to hold a BaseType value, but was holding a " + value.getClass()));
		this.value = value;
	}

	@Override
	public boolean isBaseType() {
		return BaseType.isBaseType(value);
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.matchValue(value);
	}
}
