package com.xarql.kdl.names;

public class Details implements ToBaseType {
	public final String       name;
	public final InternalName type;
	public final boolean      mutable;

	public Details(final String name, final InternalName type, final boolean mutable) {
		this.name = name;
		this.type = type;
		this.mutable = mutable;
	}

	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}
}
