package com.xarql.kdl.names;

import com.xarql.kdl.Constant;
import com.xarql.kdl.StringOutput;

import static com.xarql.kdl.names.NameFormats.internalObjectName;

public enum BaseType implements StringOutput {
	INT('I', new Constant<>(CommonNames.DEFAULT, 0)), BOOLEAN('Z', new Constant<>(CommonNames.DEFAULT, false)), STRING(internalObjectName(String.class), new Constant<>(CommonNames.DEFAULT, ""));

	String      rep;
	Constant<?> defaultValue;

	BaseType(String rep, Constant<?> defaultValue) {
		this.rep = rep;
		this.defaultValue = defaultValue;
	}

	BaseType(char rep, Constant<?> defaultValue) {
		this("" + rep, defaultValue);
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
