package com.xarql.kdl.names;

import com.xarql.kdl.CustomClass;
import com.xarql.kdl.StringOutput;
import com.xarql.kdl.UnimplementedException;

public class InternalName implements StringOutput, ToName, CommonNames {
	public static final InternalName INT_IN     = new InternalName(BaseType.INT);
	public static final InternalName BOOLEAN_IN = new InternalName(BaseType.BOOLEAN);
	public static final InternalName STRING_IN  = new InternalName(BaseType.STRING);

	public static final InternalName INT_WRAPPER     = new InternalName(Integer.class);
	public static final InternalName BOOLEAN_WRAPPER = new InternalName(Boolean.class);

	public final Class<?> clazz;
	public final BaseType base;
	public final String   qualifiedName;

	public InternalName() {
		qualifiedName = "" + ReturnValue.VOID_REP;
		this.clazz = null;
		this.base = null;
	}

	private InternalName(Class<?> c) {
		this.clazz = c;
		base = null;
		qualifiedName = null;
	}

	private InternalName(BaseType base) {
		this.base = base;
		clazz = null;
		qualifiedName = null;
	}

	public InternalName(CustomClass cc) {
		this.qualifiedName = cc.internalNameString();
		clazz = null;
		base = null;
	}

	public static InternalName internalName(Class<?> c) {
		if(BaseType.matchClass(c) != null) {
			switch(BaseType.matchClass(c)) {
				case INT:
					return INT_IN;
				case BOOLEAN:
					return BOOLEAN_IN;
				case STRING:
					return STRING_IN;
				default:
					new UnimplementedException(SWITCH_BASETYPE).printStackTrace();
					return BOOLEAN_IN;
			}
		}
		else
			return new InternalName(c);
	}

	public static InternalName match(BaseType base) {
		switch(base) {
			case INT:
				return INT_IN;
			case BOOLEAN:
				return BOOLEAN_IN;
			case STRING:
				return STRING_IN;
			default:
				throw new IllegalStateException("A BaseType to InternalName conversion is missing");
		}
	}

	public boolean isCustom() {
		return qualifiedName != null;
	}

	public boolean isClassType() {
		return clazz != null;
	}

	@Override
	public boolean isBaseType() {
		return base != null;
	}

	@Override
	public BaseType toBaseType() {
		return base;
	}

	@Override
	public String stringOutput() {
		if(clazz != null)
			return clazz.getName().replace('.', '/');
		else if(isBaseType()) {
			if(toBaseType() == STRING)
				return String.class.getName().replace('.', '/');
			else
				return toBaseType().rep;
		}
		else if(isCustom()) {
			return qualifiedName;
		}
		else
			throw new IllegalStateException("Both clazz and base can not be null in an instance of InternalName");
	}

	@Override
	public String toString() {
		return stringOutput();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof InternalName) {
			InternalName in = (InternalName) o;
			return in.stringOutput().equals(stringOutput());
		}
		else if(o instanceof BaseType) {
			BaseType bt = (BaseType) o;
			return bt == this.base;
		}
		return false;
	}

	@Override
	public InternalName toInternalName() {
		return this;
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return new InternalObjectName(this);
	}
}
