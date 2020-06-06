package com.xarql.kdl.names;

import com.xarql.kdl.Constant;
import com.xarql.kdl.StringOutput;

public enum BaseType implements StringOutput {
	INT('I', new Constant<>(CommonNames.DEFAULT, 0)), BOOLEAN('Z', new Constant<>(CommonNames.DEFAULT, false));

	char        rep;
	Constant<?> defaultValue;

	BaseType(char rep, Constant<?> defaultValue) {
		this.rep = rep;
		this.defaultValue = defaultValue;
	}

	public Constant<?> getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String stringOutput() {
		return "" + rep;
	}

	@Override
	public String toString() {
		return stringOutput();
	}
}
