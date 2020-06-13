package com.xarql.kdl;

import com.xarql.kdl.names.*;

public class Value implements ToName {
	public final ValueType  valueType;
	public final ToName content;

	public Value(ValueType valueType, ToName content) {
		if(!valueType.rep.equals(content.getClass()))
			throw new IllegalArgumentException("The ValueType " + valueType + " doesn't represent " + content.getClass());
		else {
			this.valueType = valueType;
			this.content = content;
		}
	}

	@Override
	public boolean isBaseType() {
		return content.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return content.toBaseType();
	}

	@Override
	public String toString() {
		return "Value: " + valueType + " --> " + content;
	}

	@Override
	public InternalName toInternalName() {
		return content.toInternalName();
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return content.toInternalObjectName();
	}
}
