package com.xarql.kdl;

import com.xarql.kdl.names.InternalName;

public class NameAndType {
	public final String       name;
	public final InternalName type;

	public NameAndType(String name, InternalName type) {
		this.name = name;
		this.type = type;
	}
}
