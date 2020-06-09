package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalObjectName;
import com.xarql.kdl.names.ToBaseType;

public class Variable implements ToBaseType {
	public final String             name;
	public final InternalObjectName type;
	public final int                localIndex;

	public Variable(final Scope owner, final String name, final InternalObjectName type) {
		this.name = Text.nonNull(name);
		this.type = InternalObjectName.checkNonNull(type);
		this.localIndex = owner.nextIndex();
		owner.addLocalVariable(this);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Variable) {
			Variable other = (Variable) obj;
			return other.name.equals(name);
		}
		else
			return false;
	}

	@Override
	public String toString() {
		return "LocalVariable: " + name + " --> " + type + " @ " + localIndex;
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
