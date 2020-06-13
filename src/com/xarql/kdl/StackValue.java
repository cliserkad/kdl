package com.xarql.kdl;

import com.xarql.kdl.names.*;

/**
 * Signifies that a BaseType value is on the stack
 */
public class StackValue implements ToName {
	public final BaseType type;

	public StackValue(BaseType type) {
		this.type = type;
	}

	@Override
	public boolean isBaseType() {
		return true;
	}

	@Override
	public BaseType toBaseType(){ return type;
	}

	@Override
	public InternalName toInternalName() {
		return type.toInternalName();
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return type.toInternalObjectName();
	}
}
