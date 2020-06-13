package com.xarql.kdl;

import com.xarql.kdl.names.*;

/**
 * Signifies that a reference value is on the stack
 */
public class Pointer implements ToName {
	public final InternalName internalName;

	public Pointer(InternalName internalName) {
		this.internalName = internalName;
	}

	public Pointer(InternalObjectName internalObjectName) {
		this.internalName = internalObjectName.inName;
	}

	@Override
	public boolean isBaseType() {
		return false;
	}

	@Override
	public BaseType toBaseType() {
		return null;
	}

	@Override
	public InternalName toInternalName() {
		return internalName;
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return internalName.toInternalObjectName();
	}
}
