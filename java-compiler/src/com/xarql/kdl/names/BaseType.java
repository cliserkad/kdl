package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;
import com.xarql.kdl.calculable.Constant;

import static com.xarql.kdl.names.InternalName.internalName;

public enum BaseType implements StringOutput, ToName {
	BOOLEAN('Z', new Constant<>(CommonText.DEFAULT, false)),
	BYTE('B', new Constant<>(CommonText.DEFAULT, 0)),
	SHORT('S', new Constant<>(CommonText.DEFAULT, 0)),
	CHAR('C', new Constant<>(CommonText.DEFAULT, ' ')),
	INT('I', new Constant<>(CommonText.DEFAULT, 0)),
	FLOAT('F', new Constant<>(CommonText.DEFAULT, 0.0F)),
	LONG('J', new Constant<>(CommonText.DEFAULT, 0L)),
	DOUBLE('D', new Constant<>(CommonText.DEFAULT, 0.0D)),
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

	public static BaseType matchClass(Class<?> clazz) {
		if(clazz.equals(boolean.class) || clazz.equals(Boolean.class))
			return BOOLEAN;
		else if(clazz.equals(byte.class) || clazz.equals(Byte.class))
			return BYTE;
		else if(clazz.equals(short.class) || clazz.equals(Short.class))
			return SHORT;
		else if(clazz.equals(char.class) || clazz.equals(Character.class))
			return CHAR;
		else if(clazz.equals(int.class) || clazz.equals(Integer.class))
			return INT;
		else if(clazz.equals(float.class) || clazz.equals(Float.class))
			return FLOAT;
		else if(clazz.equals(long.class) || clazz.equals(Long.class))
			return LONG;
		else if(clazz.equals(double.class) || clazz.equals(Double.class))
			return DOUBLE;
		else if(clazz.equals(String.class))
			return STRING;
		else
			return null;
	}

	public static BaseType matchValue(Object value) {
		return matchClass(value.getClass());
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
