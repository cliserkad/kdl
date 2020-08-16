package com.xarql.kdl.names;

public class NameAndType implements ToBaseType {
	public final String       name;
	public final InternalName type;

	public NameAndType(String name, InternalName type) {
		this.name = name;
		this.type = type;
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
