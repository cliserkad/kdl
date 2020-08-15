package com.xarql.kdl.names;

import static com.xarql.kdl.names.InternalObjectName.OBJECT_PREFIX;
import static com.xarql.kdl.names.InternalObjectName.OBJECT_SUFFIX;

public class CustomClass {
	public final String pkg;
	public final String name;

	public CustomClass(String pkg, String name) {
		this.pkg = pkg;
		this.name = name;
	}

	public CustomClass(String name) {
		this(null, name);
	}

	public String internalNameString() {
		return (pkg + name).replace('.', '/');
	}

	public String internalObjectNameString() {
		return OBJECT_PREFIX + internalNameString() + OBJECT_SUFFIX;
	}

	@Override
	public String toString() {
		return internalNameString();
	}
}
