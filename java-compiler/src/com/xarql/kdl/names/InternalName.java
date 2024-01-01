package com.xarql.kdl.names;

import com.xarql.kdl.AnyOf;
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
	public static final InternalName VOID = new InternalName((AnyOf<Class<?>, BaseType, CustomClass>) null, 0);
	public static final InternalName PLACEHOLDER = new InternalName(PlaceHolder.class);

	public static final InternalName STRING_BUILDER = new InternalName(StringBuilder.class);
	public static final InternalName INT_WRAPPER = new InternalName(Integer.class);
	public static final InternalName BOOLEAN_WRAPPER = new InternalName(Boolean.class);

	public static final InternalName OBJECT = new InternalName(Object.class);
	public static final InternalName ARRAY = new InternalName(Object.class, 1);

	public static final String OBJECT_SUFFIX = ";";
	public static final String OBJECT_PREFIX = "L";
	public static final String ARRAY_PREFIX = "[";
	public static final char INTERNAL_SEPARATOR = '/';
	public static final char SOURCE_SEPARATOR = '.';
	public static final String INTERNAL_SEPARATOR_STRING = "" + INTERNAL_SEPARATOR;
	public static final String SOURCE_SEPARATOR_STRING = "" + SOURCE_SEPARATOR;
	public static final int DEFAULT_ARRAY_DIMENSIONS = 0;
	public static final int MIN_DIMENSIONS = 0;
	public static final int MAX_DIMENSIONS = 255;
	public static final String ARRAY_DIMENSIONS_OUT_OF_BOUNDS_MSG = "array dimensions must be within " + MIN_DIMENSIONS + " & " + MAX_DIMENSIONS;
	public static final String ARRAY_MAY_NOT_BE_OF_TYPE_VOID_MSG = "This InternalName represents void, which is not a valid array type.";

	public final AnyOf<Class<?>, BaseType, CustomClass> data;
	public final int arrayDimensions;

	public InternalName(final AnyOf<Class<?>, BaseType, CustomClass> data, final int arrayDimensions) {
		this.data = data;
		checkArrayDimensions(arrayDimensions);
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final Class<?> c, final int arrayDimensions) {
		this(convertIfNecessary(c), arrayDimensions);
	}

	public InternalName(final Class<?> c) {
		this(c, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final BaseType base) {
		this(new AnyOf.ElementB<>(base), DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final CustomClass cc) {
		this(new AnyOf.ElementC<>(cc), DEFAULT_ARRAY_DIMENSIONS);
	}

	public static AnyOf<Class<?>, BaseType, CustomClass> convertIfNecessary(final Class<?> c) {
		if(BaseType.matchClassStrict(c) != null) {
			return new AnyOf.ElementB<>(BaseType.matchClass(c));
		} else {
			return new AnyOf.ElementA<>(c);
		}
	}

	public static void checkArrayDimensions(final int arrayDimensions) throws IllegalArgumentException {
		if(arrayDimensions < MIN_DIMENSIONS || arrayDimensions > MAX_DIMENSIONS)
			throw new IllegalArgumentException(ARRAY_DIMENSIONS_OUT_OF_BOUNDS_MSG);
	}

	public boolean isCustom() {
		// check that data has the 3rd type of the below parameterized ElementC
		// you can use <> to infer all types, but that is less explicit for the reader
		// we don't care what the other elements are allowed to hold, so we infer them with ?
		return data instanceof AnyOf.ElementC<?, ?, CustomClass>;
	}

	public boolean isClassType() {
		// see isCustom() for explanation
		return data instanceof AnyOf.ElementA<Class<?>, ?, ?>;
	}

	@Override
	public boolean isBaseType() {
		return data instanceof AnyOf.ElementB<?, BaseType, ?>;
	}

	@Override
	public BaseType toBaseType() {
		if(data instanceof AnyOf.ElementB<?, BaseType, ?> base)
			return base.getValue();
		else
			return null;
	}

	private String objectInstance() {
		return OBJECT_PREFIX + nameString() + OBJECT_SUFFIX;
	}

	public String objectString() {
		final String dims = ARRAY_PREFIX.repeat(arrayDimensions);

		if(isBaseType() && toBaseType() != BaseType.STRING)
			return dims + nameString();
		else
			return dims + objectInstance();
	}

	public String nameString() {
		if(data == null)
			return ReturnValue.VOID_REP_STRING;
		else {
			return data.match((clazz -> {
				return qualifiedName(clazz);
			}), (baseType -> {
				if(baseType == BaseType.STRING)
					return qualifiedName(String.class);
				else
					return baseType.rep;
			}), (CustomClass::qualifiedName));
		}
	}

	public String qualifiedName(Class<?> c) {
		return c.getName().replace(SOURCE_SEPARATOR, INTERNAL_SEPARATOR);
	}

	@Override
	public String toString() {
		return objectString();
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		} else if(o == this) {
			return true;
		} else if(o instanceof InternalName name) {
			return name.data.equals(data) && name.arrayDimensions == arrayDimensions;
		} else if(o instanceof BaseType bt) {
			return bt == toBaseType();
		} else {
			return false;
		}
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

	public InternalName toArray(final int dimensions) throws IllegalArgumentException {
		if(data == null) {
			throw new IllegalArgumentException(ARRAY_MAY_NOT_BE_OF_TYPE_VOID_MSG);
		} else {
			return new InternalName(data, dimensions);
		}
	}

	public InternalName withoutArray() {
		return new InternalName(data, MIN_DIMENSIONS);
	}

	public boolean matchesClassname(String classname) {
		final String str = nameString();
		return str.contains(INTERNAL_SEPARATOR_STRING) && str.lastIndexOf(INTERNAL_SEPARATOR_STRING) + 1 <= str.length() && str.substring(str.lastIndexOf(INTERNAL_SEPARATOR_STRING) + 1).equals(classname);
	}

}
