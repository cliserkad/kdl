package com.xarql.kdl.ir;

import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;

public class Field extends Details {

	public Field(Details details) {
		super(details);
	}

	public Field(String name, InternalName type, boolean mutable) {
		super(name, type, mutable);
	}

}
