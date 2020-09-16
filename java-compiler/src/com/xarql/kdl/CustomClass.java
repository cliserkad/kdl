package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;

public class CustomClass implements ToName {

	public final String pkg;
	public final String name;

	public CustomClass(String pkg, String name) {
		this.pkg = pkg;
		this.name = name;
	}

	public CustomClass(String name) {
		this(null, name);
	}

	@Override
	public String toString() {
		return toInternalName().objectString();
	}

	@Override
	public InternalName toInternalName() {
		return new InternalName(this);
	}

	@Override
	public boolean isBaseType() {
		return false;
	}

	@Override
	public BaseType toBaseType() {
		return null;
	}

}
