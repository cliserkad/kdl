package com.xarql.kdl.names;

import com.xarql.kdl.Actor;
import com.xarql.kdl.CustomClass;
import com.xarql.kdl.PlaceHolder;

public class InternalName implements ToName, CommonText {

	public static final InternalName BOOLEAN = new InternalName(BaseType.BOOLEAN);
	public static final InternalName BYTE = new InternalName(BaseType.BYTE);
	public static final InternalName SHORT = new InternalName(BaseType.SHORT);
	public static final InternalName CHAR = new InternalName(BaseType.CHAR);
	public static final InternalName INT = new InternalName(BaseType.INT);
	public static final InternalName FLOAT = new InternalName(BaseType.FLOAT);
	public static final InternalName LONG = new InternalName(BaseType.LONG);
	public static final InternalName DOUBLE = new InternalName(BaseType.DOUBLE);
	public static final InternalName STRING = new InternalName(BaseType.STRING);
	public static final InternalName PLACEHOLDER = new InternalName(PlaceHolder.class);

	public static final InternalName STRING_BUILDER = new InternalName(StringBuilder.class);
	public static final InternalName INT_WRAPPER = new InternalName(Integer.class);
	public static final InternalName BOOLEAN_WRAPPER = new InternalName(Boolean.class);

	public static final InternalName OBJECT = new InternalName(Object.class);
	public static final InternalName ARRAY = new InternalName(Object.class, 1);

	public static final String OBJECT_SUFFIX = ";";
	public static final String OBJECT_PREFIX = "L";
	public static final String ARRAY_PREFIX = "[";
	public static final int DEFAULT_ARRAY_DIMENSIONS = 0;
	public static final int MIN_DIMENSIONS = 0;
	public static final int MAX_DIMENSIONS = 255;

	public final Class<?> clazz;
	public final BaseType base;
	public final CustomClass cc;

	public final int arrayDimensions;

	public InternalName() {
		clazz = null;
		base = null;
		cc = null;
		arrayDimensions = DEFAULT_ARRAY_DIMENSIONS;
	}

	public InternalName(final Class<?> c, final int arrayDimensions) {
		if(BaseType.matchClassStrict(c) != null) {
			base = BaseType.matchClass(c);
			clazz = null;
		} else {
			clazz = c;
			base = null;
		}
		cc = null;
		if(arrayDimensions < MIN_DIMENSIONS || arrayDimensions > MAX_DIMENSIONS)
			throw new IllegalArgumentException("arrayDimensions must be within " + MIN_DIMENSIONS + " & " + MAX_DIMENSIONS);
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final Class<?> c) {
		this(c, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final BaseType base, final int arrayDimensions) {
		this.base = base;
		clazz = null;
		cc = null;
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final BaseType base) {
		this(base, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final CustomClass cc) {
		this.cc = cc;
		clazz = null;
		base = null;
		arrayDimensions = DEFAULT_ARRAY_DIMENSIONS;
	}

	public boolean isCustom() {
		return cc != null;
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
		return OBJECT_PREFIX + nameString() + OBJECT_SUFFIX;
	}

	public String objectString() {
		String dims = "";
		for(int i = 0; i < arrayDimensions; i++)
			dims += ARRAY_PREFIX;

		if(isBaseType() && toBaseType() != BaseType.STRING)
			return dims + nameString();
		else
			return dims + objectInstance();
	}

	public String nameString() {
		if(isBaseType()) {
			if(toBaseType() == BaseType.STRING)
				return String.class.getName().replace('.', '/');
			else
				return toBaseType().rep;
		} else if(isClassType())
			return clazz.getName().replace('.', '/');
		else if(isCustom())
			return cc.qualifiedName();
		else
			return "" + ReturnValue.VOID_REP;
	}

	@Override
	public String toString() {
		return objectString();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof InternalName) {
			InternalName in = (InternalName) o;
			return in.nameString().equals(nameString());
		} else if(o instanceof BaseType) {
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
		return arrayDimensions > MIN_DIMENSIONS;
	}

	public InternalName toArray(final int dimensions) {
		if(isClassType())
			return new InternalName(clazz, dimensions);
		else if(isBaseType())
			return new InternalName(base, dimensions);
		else if(isCustom())
			return new InternalName(cc);
		else
			throw new IllegalArgumentException("This InternalName represents void, which is not a valid array type.");
	}

	public InternalName withoutArray() {
		if(isClassType())
			return new InternalName(clazz);
		else if(isBaseType())
			return new InternalName(base);
		else if(isCustom())
			return new InternalName(cc);
		else
			return new InternalName();
	}

}
