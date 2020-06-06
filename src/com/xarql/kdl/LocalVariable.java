package com.xarql.kdl;

import com.xarql.kdl.names.InternalObjectName;

public class LocalVariable {
	public final String             name;
	public final InternalObjectName type;
	public final int                localIndex;

	public LocalVariable(final Scope owner, final String name, final InternalObjectName type) {
		this.name = Text.nonNull(name);
		this.type = InternalObjectName.checkNonNull(type);
		this.localIndex = owner.nextIndex();
		owner.addLocalVariable(this);
	}

	@Override
	public String toString() {
		return "LocalVariable: " + name + " --> " + type + " @ " + localIndex;
	}

}
