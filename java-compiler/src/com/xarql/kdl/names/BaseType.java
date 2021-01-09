package com.xarql.kdl.names;

import com.xarql.kdl.Path;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.ir.Literal;
import org.objectweb.asm.Opcodes;

public enum BaseType implements ToName {

	BOOLEAN('Z', false, Opcodes.T_BOOLEAN),
	BYTE('B', 0, Opcodes.T_BYTE),
	SHORT('S', 0, Opcodes.T_SHORT),
	CHAR('C', ' ', Opcodes.T_CHAR),
	INT('I', 0, Opcodes.T_INT),
	FLOAT('F', 0.0F, Opcodes.T_FLOAT),
	LONG('J', 0L, Opcodes.T_LONG),
	DOUBLE('D', 0.0D, Opcodes.T_DOUBLE),
	STRING("Ljava/lang/String;", "", 0);

	public final Path rep;
	private final Object defaultValue;
	public final int id;

	BaseType(String rep, Object defaultValue, int id) {
		this.rep = new Path(rep);
		this.defaultValue = defaultValue;
		this.id = id;
	}

	BaseType(char rep, Object defaultValue, int id) {
		this("" + rep, defaultValue, id);
	}

	/**
	 * Matches a primitive or wrapper class to a BaseType
	 *
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
	 *
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

	public static BaseType matchPath(final Path path) {
		for(BaseType base : values()) {
			if(base.rep.equals(path))
				return base;
		}
		return null;
	}

	public static BaseType matchValue(Object value) {
		return matchClass(value.getClass());
	}

	public static boolean isBaseType(Class<?> clazz) {
		return matchClass(clazz) != null;
	}

	public static boolean isBaseType(final Object value) {
		return matchValue(value) != null;
	}

	public static boolean isBaseType(final Path path) {
		return matchPath(path) != null;
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

	public Literal<?> getDefaultValue() {
		return new Literal<>(defaultValue);
	}

	@Override
	public String toString() {
		return toInternalName().arrayName();
	}

	@Override
	public InternalName toInternalName() {
		switch(this) {
			case BOOLEAN:
				return InternalName.BOOLEAN;
			case BYTE:
				return InternalName.BYTE;
			case SHORT:
				return InternalName.SHORT;
			case CHAR:
				return InternalName.CHAR;
			case INT:
				return InternalName.INT;
			case FLOAT:
				return InternalName.FLOAT;
			case LONG:
				return InternalName.LONG;
			case DOUBLE:
				return InternalName.DOUBLE;
			case STRING:
				return InternalName.STRING;
			default:
				return null;
		}
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
