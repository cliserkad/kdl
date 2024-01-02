package com.xarql.kdl.names;

import com.xarql.kdl.ir.Literal;
import org.objectweb.asm.Opcodes;

public enum BaseType implements ToInternalName {

	BOOLEAN('Z', Opcodes.T_BOOLEAN, boolean.class, Boolean.class),
	BYTE('B', Opcodes.T_BYTE, byte.class, Byte.class),
	SHORT('S', Opcodes.T_SHORT, short.class, Short.class),
	CHAR('C', Opcodes.T_CHAR, char.class, Character.class),
	INT('I', Opcodes.T_INT, int.class, Integer.class),
	FLOAT('F', Opcodes.T_FLOAT, float.class, Float.class),
	LONG('J', Opcodes.T_LONG, long.class, Long.class),
	DOUBLE('D', Opcodes.T_DOUBLE, double.class, Double.class),
	STRING("Ljava/lang/String;", 0, String.class, String.class);

	public final String rep;
	public final int id;
	public final Class<?> primitiveClass;
	public final Class<?> wrapperClass;

	BaseType(String rep, int id, Class<?> primitiveClass, Class<?> wrapperClass) {
		this.rep = rep;
		this.id = id;
		this.primitiveClass = primitiveClass;
		this.wrapperClass = wrapperClass;
	}

	BaseType(char rep, int id, Class<?> primitiveClass, Class<?> wrapperClass) {
		this("" + rep, id, primitiveClass, wrapperClass);
	}

	public static boolean isBaseType(final Object value) {
		final Class<?> c = value.getClass();
		return matchClass(c) != null;
	}

	/**
	 * Matches a primitive or wrapper class to a BaseType
	 *
	 * @param c any Class
	 * @return BaseType on match, null otherwise
	 */
	public static BaseType matchClass(Class<?> c) {
		for(final BaseType baseType : values())
			if(baseType.primitiveClass.equals(c) || baseType.wrapperClass.equals(c))
				return baseType;
		return null;
	}

	/**
	 * Match only primitive classes, not their wrappers
	 *
	 * @param c any Class
	 * @return BaseType on match, null otherwise
	 */
	public static BaseType matchClassStrict(final Class<?> c) {
		for(final BaseType baseType : values())
			if(baseType.primitiveClass.equals(c))
				return baseType;
		return null;
	}

	public static BaseType matchValue(Object value) {
		return matchClass(value.getClass());
	}

	@Override
	public InternalName toInternalName() {
		return new InternalName(this);
	}

	public Literal<?> getDefaultValue() {
		return Literal.getDefaultForBaseType(this);
	}

	public boolean compatibleNoDirection(ToInternalName other) {
		if(!other.isBaseType())
			return false;
		else
			return compatibleNoDirection(other.toBaseType());
	}

	public boolean compatibleNoDirection(BaseType other) {
		return this.compatibleWith(other) || other.compatibleWith(this);
	}

	public boolean compatibleWith(ToInternalName receiver) {
		if(!receiver.isBaseType())
			return false;
		else
			return compatibleWith(receiver.toBaseType());
	}

	public boolean compatibleWith(BaseType receiver) {
		return ordinal() <= receiver.ordinal();
	}

	@Override
	public String toString() {
		return toInternalName().objectString();
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
