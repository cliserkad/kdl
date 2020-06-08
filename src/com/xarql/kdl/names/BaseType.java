package com.xarql.kdl.names;

import com.xarql.kdl.Constant;
import com.xarql.kdl.StringOutput;

public enum BaseType implements StringOutput {
	INT('I', new Constant<>(CommonNames.DEFAULT, 0)), BOOLEAN('Z', new Constant<>(CommonNames.DEFAULT, false)), STRING(new InternalObjectName(String.class).toString(), new Constant<>(CommonNames.DEFAULT, ""));

	String      rep;
	Constant<?> defaultValue;

	BaseType(String rep, Constant<?> defaultValue) {
		this.rep = rep;
		this.defaultValue = defaultValue;
	}

	BaseType(char rep, Constant<?> defaultValue) {
		this("" + rep, defaultValue);
	}

	public static boolean isBaseType(Class<?> base) {
		return matchWrapper(base) != null;
	}

	public static BaseType matchWrapper(Class<?> wrapper) {
		if(wrapper == Integer.class)
			return INT;
		else if(wrapper == Boolean.class)
			return BOOLEAN;
		else if(wrapper == String.class)
			return STRING;
		else
			return null;
	}

	public InternalName toInternalName() {
		return InternalName.match(this);
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
