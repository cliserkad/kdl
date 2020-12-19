package com.xarql.kdl.ir;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.Details;

public class Param extends Details {

	public final kdl.ExpressionContext defaultValue;

	public Param(Details details, kdl.ExpressionContext defaultValue) {
		super(details);
		this.defaultValue = defaultValue;
	}

	public Param(Details details) {
		this(details, null);
	}
}
