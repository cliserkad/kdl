package com.xarql.kdl.names;

public class Details implements ToName {
	public final String       name;
	public final InternalName type;
	public final boolean      mutable;

	public Details(final String name, final InternalName type, final boolean mutable) {
		this.name = name;
		this.type = type;
		this.mutable = mutable;
	}


	/**
	 * Forwarding method
	 * @see InternalName#isBaseType()
	 */
	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}


	/**
	 * Forwarding method
	 * @see InternalName#toBaseType()
	 */
	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}


	/**
	 * @return type
	 */
	@Override
	public InternalName toInternalName() {
		return type;
	}
}
