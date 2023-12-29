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

	public final AnyOf<Class<?>, BaseType, CustomClass> data;
	public final int arrayDimensions;

	public InternalName(final AnyOf<Class<?>, BaseType, CustomClass> data, final int arrayDimensions) {
		this.data = data;
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName() {
		this((AnyOf<Class<?>, BaseType, CustomClass>) null, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final Class<?> c, final int arrayDimensions) {
		if(BaseType.matchClassStrict(c) != null) {
			data = new AnyOf.ElementB<>(BaseType.matchClass(c));
		} else {
			data = new AnyOf.ElementA<>(c);
		}
		if(arrayDimensions < MIN_DIMENSIONS || arrayDimensions > MAX_DIMENSIONS)
			throw new IllegalArgumentException("arrayDimensions must be within " + MIN_DIMENSIONS + " & " + MAX_DIMENSIONS);
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final Class<?> c) {
		this(c, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final BaseType base, final int arrayDimensions) {
		data = new AnyOf.ElementB<>(base);
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final BaseType base) {
		this(base, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final CustomClass cc) {
		data = new AnyOf.ElementC<>(cc);
		arrayDimensions = DEFAULT_ARRAY_DIMENSIONS;
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
			return "" + ReturnValue.VOID_REP;
		else {
			return data.match((clazz -> {
				return clazz.getName().replace('.', '/');
			}), (baseType -> {
				if(baseType == BaseType.STRING)
					return String.class.getName().replace('.', '/');
				else
					return baseType.rep;
			}), (CustomClass::qualifiedName));
		}
	}

	@Override
	public String toString() {
		return objectString();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof InternalName name) {
			return name.nameString().equals(nameString());
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
			throw new IllegalArgumentException("This InternalName represents void, which is not a valid array type.");
		} else {
			return new InternalName(data, dimensions);
		}
	}

	public InternalName withoutArray() {
		return new InternalName(data, MIN_DIMENSIONS);
	}

	public boolean matchesClassname(String classname) {
		final String str = nameString();
		return str.contains("/") && str.lastIndexOf("/") + 1 <= str.length() && str.substring(str.lastIndexOf("/") + 1).equals(classname);
	}

}
