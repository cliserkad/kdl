package com.xarql.kdl.names;

import com.xarql.kdl.CustomClass;
import com.xarql.kdl.StringOutput;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.calculable.Array;

public class InternalName implements ToName, CommonText {
	public static final InternalName BOOLEAN = new InternalName(BaseType.BOOLEAN);
	public static final InternalName BYTE    = new InternalName(BaseType.BYTE);
	public static final InternalName SHORT   = new InternalName(BaseType.SHORT);
	public static final InternalName CHAR    = new InternalName(BaseType.CHAR);
	public static final InternalName INT     = new InternalName(BaseType.INT);
	public static final InternalName FLOAT   = new InternalName(BaseType.FLOAT);
	public static final InternalName LONG    = new InternalName(BaseType.LONG);
	public static final InternalName DOUBLE  = new InternalName(BaseType.DOUBLE);
	public static final InternalName STRING  = new InternalName(BaseType.STRING);

	public static final InternalName STRING_BUILDER  = new InternalName(StringBuilder.class);
	public static final InternalName INT_WRAPPER     = new InternalName(Integer.class);
	public static final InternalName BOOLEAN_WRAPPER = new InternalName(Boolean.class);

	public static final InternalName OBJECT = new InternalName(Object.class);

	public static final String OBJECT_SUFFIX            = ";";
	public static final String OBJECT_PREFIX            = "L";
	public static final String ARRAY_PREFIX             = "[";
	public static final int    DEFAULT_ARRAY_DIMENSIONS = 0;

	public final Class<?> clazz;
	public final BaseType base;
	public final String   qualifiedName;

	public final int arrayDimensions;

	public InternalName() {
		qualifiedName = "" + ReturnValue.VOID_REP;
		this.clazz = null;
		this.base = null;
		arrayDimensions = DEFAULT_ARRAY_DIMENSIONS;
	}

	public InternalName(final Class<?> c, final int arrayDimensions) {
		this.clazz = c;
		base = null;
		qualifiedName = null;
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final Class<?> c) {
		this(c, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final BaseType base, final int arrayDimensions) {
		this.base = base;
		this.clazz = null;
		this.qualifiedName = null;
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final BaseType base) {
		this(base, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(CustomClass cc) {
		this.qualifiedName = cc.internalNameString();
		clazz = null;
		base = null;
		arrayDimensions = DEFAULT_ARRAY_DIMENSIONS;
	}

	public boolean isCustom() {
		return qualifiedName != null && !qualifiedName.equals("" + ReturnValue.VOID_REP);
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

	private String objectInstance() {
		return OBJECT_PREFIX + internalName() + OBJECT_SUFFIX;
	}

	public String internalObjectName() {
		String dims = "";
		for(int i = 0; i < arrayDimensions; i++)
			dims += ARRAY_PREFIX;

		if(isBaseType() && toBaseType() != BaseType.STRING)
			return dims + internalName();
		else
			return dims + objectInstance();
	}

	public String internalName() {
		if(clazz != null)
			return clazz.getName().replace('.', '/');
		else if(isBaseType()) {
			if(toBaseType() == BaseType.STRING)
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
		return internalObjectName();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof InternalName) {
			InternalName in = (InternalName) o;
			return in.internalName().equals(internalName());
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

	public boolean compatibleWith(InternalName receiver) {
		if(receiver.toBaseType() == BaseType.STRING)
			return true;
		else if(toBaseType() == BaseType.STRING && receiver.equals(new InternalName(CharSequence.class)))
			return true;
		else if(receiver.isBaseType() && isBaseType())
			return toBaseType().compatibleWith(receiver);
		else
			return equals(receiver);
	}

	public boolean isArray() {
		return arrayDimensions >= Array.MIN_DIMENSIONS;
	}

	public Array toArray() {
		if(!isArray())
			throw new IllegalArgumentException("This InternalName isn't an array");
		return new Array(this, arrayDimensions);
	}

}
