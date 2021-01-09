package com.xarql.kdl.names;

import com.xarql.kdl.Type;

public interface ToType {

	Type toType();

	boolean isBaseType();

	BaseType toBaseType();

}
