package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;
import com.xarql.kdl.UnimplementedException;

import static java.lang.System.exit;

public class InternalName implements StringOutput, CommonNames {
	public static final InternalName INT_IN     = new InternalName(BaseType.INT);
	public static final InternalName BOOLEAN_IN = new InternalName(BaseType.BOOLEAN);
	public static final InternalName STRING_IN  = new InternalName(BaseType.STRING);

	public final Class<?> clazz;
	public final BaseType base;

	private InternalName(Class<?> c) {
		this.clazz = c;
		base = null;
	}

	private InternalName(BaseType base) {
		this.base = base;
		clazz = null;
	}

	public static InternalName internalName(Class<?> c) {
		try {
			if(BaseType.matchWrapper(c) != null) {
				switch(BaseType.matchWrapper(c)) {
					case INT:
						return INT_IN;
					case BOOLEAN:
						return BOOLEAN_IN;
					case STRING:
						return STRING_IN;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			}
			else
				return new InternalName(c);
		} catch(UnimplementedException ue) {
			System.err.println(ue.getMessage());
			ue.printStackTrace();
			exit(1);
			return null;
		}
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

	public InternalObjectName object() {
		return new InternalObjectName(this);
	}

	public boolean isClassType() {
		return clazz != null;
	}

	public boolean isBaseType() {
		return base != null;
	}

	@Override
	public String stringOutput() {
		if(clazz != null)
			return clazz.getName().replace('.', '/');
		else if(isBaseType())
			return base.stringOutput();
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
}
