package com.xarql.kdl.names;

public interface ToInternalName {

	InternalName toInternalName();

	boolean isBaseType();

	BaseType toBaseType();

}
