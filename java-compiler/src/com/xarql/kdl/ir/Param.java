package com.xarql.kdl.ir;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.Details;

public class Param extends Details {

	public final kdl.ValueContext defaultValue;

	public Param(Details details, kdl.ValueContext defaultValue) {
		super(details);
		this.defaultValue = defaultValue;
	}

	public Param(Details details) {
		this(details, null);
	}

}
