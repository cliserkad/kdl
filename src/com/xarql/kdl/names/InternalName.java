package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;

public class InternalName implements StringOutput {
	public static final InternalName INT     = new InternalName(BaseType.INT);
	public static final InternalName BOOLEAN = new InternalName(BaseType.BOOLEAN);

	public final Class<?> clazz;
	public final BaseType base;

	public InternalName(Class<?> c) {
		this.clazz = c;
		base = null;
	}

	private InternalName(BaseType base) {
		this.base = base;
		clazz = null;
	}

	public InternalObjectName object() {
		return new InternalObjectName(this);
	}

	public boolean isClassType() {
		return clazz != null;
	}

	public boolean isBaseType() {
		return base != null;
	}

	@Override
	public String stringOutput() {
		if(clazz != null)
			return clazz.getName().replace('.', '/');
		else if(isBaseType())
			return base.stringOutput();
		else
			throw new IllegalStateException("Both clazz and base can not be null in an instance of InternalName");
	}

	@Override
	public String toString() {
		return stringOutput();
	}
}
