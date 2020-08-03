package com.xarql.kdl.names;

import com.xarql.kdl.Constant;
import com.xarql.kdl.StringOutput;

import static com.xarql.kdl.names.InternalName.internalName;

public enum BaseType implements StringOutput {
	INT('I', new Constant<>(CommonNames.DEFAULT, 0)), BOOLEAN('Z', new Constant<>(CommonNames.DEFAULT, false)), STRING(internalName(String.class).toInternalObjectName().stringOutput(), new Constant<>(CommonNames.DEFAULT, ""));

	String      rep;
	Constant<?> defaultValue;

	BaseType(String rep, Constant<?> defaultValue) {
		this.rep = rep;
		this.defaultValue = defaultValue;
	}

	BaseType(char rep, Constant<?> defaultValue) {
		this("" + rep, defaultValue);
	}

	public static boolean isClassBaseType(Class<?> clazz) {
		return matchClass(clazz) != null;
	}

	public static boolean isBaseType(Object value) {
		return matchValue(value) != null;
	}

	public static BaseType matchClass(Class<?> clazz) {
		if(clazz.equals(int.class) || clazz.equals(Integer.class))
			return INT;
		else if(clazz.equals(boolean.class) || clazz.equals(Boolean.class))
			return BOOLEAN;
		else if(clazz.equals(String.class))
			return STRING;
		else
			return null;
	}

	public static BaseType matchValue(Object value) {
		if(value instanceof Integer)
			return INT;
		else if(value instanceof Boolean)
			return BOOLEAN;
		else if(value instanceof String)
			return STRING;
		else
			return null;
	}

	public InternalName toInternalName() {
		return InternalName.match(this);
	}

	public InternalObjectName toInternalObjectName() {
		return toInternalName().toInternalObjectName();
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
