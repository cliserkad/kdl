package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;
import com.xarql.kdl.ir.Constant;

public enum BaseType implements StringOutput, ToName {
	BOOLEAN('Z', new Constant<>(CommonText.DEFAULT, false)), BYTE('B', new Constant<>(CommonText.DEFAULT, 0)), SHORT('S', new Constant<>(CommonText.DEFAULT, 0)), CHAR('C', new Constant<>(CommonText.DEFAULT, ' ')),
	INT('I', new Constant<>(CommonText.DEFAULT, 0)), FLOAT('F', new Constant<>(CommonText.DEFAULT, 0.0F)), LONG('J', new Constant<>(CommonText.DEFAULT, 0L)), DOUBLE('D', new Constant<>(CommonText.DEFAULT, 0.0D)),
	STRING("Ljava/lang/String;", new Constant<>(CommonText.DEFAULT, ""));

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

	/**
	 * Matches a primitive or wrapper class to a BaseType
	 * @param c any Class
	 * @return BaseType on match, null otherwise
	 */
	public static BaseType matchClass(Class<?> c) {
		if(c.equals(boolean.class) || c.equals(Boolean.class))
			return BOOLEAN;
		else if(c.equals(byte.class) || c.equals(Byte.class))
			return BYTE;
		else if(c.equals(short.class) || c.equals(Short.class))
			return SHORT;
		else if(c.equals(char.class) || c.equals(Character.class))
			return CHAR;
		else if(c.equals(int.class) || c.equals(Integer.class))
			return INT;
		else if(c.equals(float.class) || c.equals(Float.class))
			return FLOAT;
		else if(c.equals(long.class) || c.equals(Long.class))
			return LONG;
		else if(c.equals(double.class) || c.equals(Double.class))
			return DOUBLE;
		else if(c.equals(String.class))
			return STRING;
		else
			return null;
	}

	/**
	 * Match only primitive classes, not their wrappers
	 * @param c any Class
	 * @return BaseType on match, null otherwise
	 */
	public static BaseType matchClassStrict(final Class<?> c) {
		if(c.equals(boolean.class))
			return BOOLEAN;
		else if(c.equals(byte.class))
			return BYTE;
		else if(c.equals(short.class))
			return SHORT;
		else if(c.equals(char.class))
			return CHAR;
		else if(c.equals(int.class))
			return INT;
		else if(c.equals(float.class))
			return FLOAT;
		else if(c.equals(long.class))
			return LONG;
		else if(c.equals(double.class))
			return DOUBLE;
		else if(c.equals(String.class))
			return STRING;
		else
			return null;
	}

	public static BaseType matchValue(Object value) {
		return matchClass(value.getClass());
	}

	@Override
	public InternalName toInternalName() {
		return new InternalName(this);
	}

	public Constant<?> getDefaultValue() {
		return defaultValue;
	}

	public boolean compatibleNoDirection(ToName other) {
		if(!other.isBaseType())
			return false;
		else
			return compatibleNoDirection(other.toBaseType());
	}

	public boolean compatibleNoDirection(BaseType other) {
		return this.compatibleWith(other) || other.compatibleWith(this);
	}

	public boolean compatibleWith(ToName receiver) {
		if(!receiver.isBaseType())
			return false;
		else
			return compatibleWith(receiver.toBaseType());
	}

	public boolean compatibleWith(BaseType receiver) {
		return ordinal() <= receiver.ordinal();
	}

	@Override
	public String stringOutput() {
		return "" + rep;
	}

	@Override
	public String toString() {
		return stringOutput();
	}

	@Override
	public boolean isBaseType() {
		return true;
	}

	@Override
	public BaseType toBaseType() {
		return this;
	}
}
