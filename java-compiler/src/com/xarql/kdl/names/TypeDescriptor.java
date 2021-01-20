package com.xarql.kdl.names;

import com.xarql.smp.Path;
import com.xarql.kdl.PlaceHolder;
import com.xarql.kdl.Type;

import java.util.Objects;

/**
 * Stores a Type and its Array dimensions
 */
public class TypeDescriptor implements ToTypeDescriptor, CommonText {

	public static final TypeDescriptor VOID = new TypeDescriptor();
	public static final TypeDescriptor INT_WRAPPER = new TypeDescriptor(Integer.class);
	public static final TypeDescriptor OBJECT = new TypeDescriptor(Object.class);
	public static final TypeDescriptor PLACEHOLDER = new TypeDescriptor(PlaceHolder.class);

	public static final Type VOID_TYPE = null;
	public static final String VOID_REP = "V";
	public static final String OBJECT_SUFFIX = ";";
	public static final String OBJECT_PREFIX = "L";
	public static final String ARRAY_PREFIX = "[";
	public static final int DEFAULT_ARRAY_DIMENSIONS = 0;
	public static final int MIN_DIMENSIONS = 0;
	public static final int MAX_DIMENSIONS = 255;

	public final Type type;
	public final int arrayDimensions;

	private TypeDescriptor() {
		type = VOID_TYPE;
		arrayDimensions = DEFAULT_ARRAY_DIMENSIONS;
	}

	public TypeDescriptor(final Type type) {
		this(type, DEFAULT_ARRAY_DIMENSIONS);
	}

	public TypeDescriptor(final Type type, final int arrayDimensions) {
		if(type == null)
			throw new IllegalArgumentException("type may not be null");
		if(arrayDimensions < MIN_DIMENSIONS || arrayDimensions > MAX_DIMENSIONS)
			throw new IllegalArgumentException("int arrayDimensions must be within " + MIN_DIMENSIONS + " & " + MAX_DIMENSIONS);
		this.type = type;
		this.arrayDimensions = arrayDimensions;
	}

	public TypeDescriptor(final Class<?> c, final int arrayDimensions) {
		this(Type.get(c), arrayDimensions);
	}

	public TypeDescriptor(final Class<?> c) {
		this(c, DEFAULT_ARRAY_DIMENSIONS);
	}

	public TypeDescriptor(final BaseType baseType, final int arrayDimensions) {
		this.type = baseType.toType();
		this.arrayDimensions = arrayDimensions;
	}

	public Object defaultValue() {
		if(isBaseType())
			return toBaseType().getDefaultValue();
		else
			return null;
	}

	public boolean isVoid() {
		return type == VOID_TYPE;
	}

	@Override
	public boolean isBaseType() {
		return BaseType.isBaseType(type);
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.matchPath(type.name);
	}

	/**
	 * Provides the qualified name, surrounded with object markers. If this
	 * InternalName represents a BaseType which is not string, then qualifiedName()
	 * is returned. Ex: Ljava/lang/String;
	 */
	public String objectName() {
		if(isVoid())
			return VOID_REP;
		else if(isBaseType() && toBaseType() != BaseType.STRING)
			return qualifiedName();
		else
			return OBJECT_PREFIX + qualifiedName() + OBJECT_SUFFIX;
	}

	/**
	 * Provides the objectName, prefixed with arrayDimensions amount of array
	 * dimension markers. Ex: [[Ljava/lang/String;
	 */
	public String arrayName() {
		StringBuilder dims = new StringBuilder();
		for(int i = 0; i < arrayDimensions; i++)
			dims.append(ARRAY_PREFIX);
		dims.append(objectName());
		return dims.toString();
	}

	/**
	 * Provides the qualified name. Ex: java/lang/String
	 */
	public String qualifiedName() {
		if(isVoid())
			return VOID_REP;
		else
			return type.toString();
	}

	/**
	 * Provides the type name of this InternalName. Ex: String
	 */
	public String name() {
		return type.name.last();
	}

	@Override
	public String toString() {
		return arrayName();
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return this.equals(TypeDescriptor.VOID);
		} else if(this == o) {
			return true;
		} else if(o instanceof TypeDescriptor) {
			TypeDescriptor in = (TypeDescriptor) o;
			return in.type.equals(type);
		} else if(o instanceof BaseType) {
			BaseType base = (BaseType) o;
			return base.toType().equals(this.type);
		} else if(o instanceof String) {
			return type.name.last().equals(o);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, arrayDimensions);
	}

	@Override
	public Type toType() {
		return type;
	}

	public boolean compatibleWith(TypeDescriptor receiver) {
		if(receiver.toBaseType() == BaseType.STRING)
			return true;
		else if(toBaseType() == BaseType.STRING && receiver.equals(new TypeDescriptor(CharSequence.class)))
			return true;
		else if(receiver.isBaseType() && isBaseType())
			return toBaseType().compatibleWith(receiver);
		else
			return equals(receiver);
	}

	public boolean isArray() {
		return arrayDimensions > MIN_DIMENSIONS;
	}

	public TypeDescriptor toArray(final int dimensions) {
		if(isVoid())
			throw new IllegalArgumentException("This InternalName represents void, which is not a valid array type.");
		else {
			return new TypeDescriptor(type, dimensions);
		}
	}

	public TypeDescriptor withoutArray() {
		if(isArray()) {
			return new TypeDescriptor(type);
		} else {
			return this;
		}
	}

	@Override
	public TypeDescriptor toTypeDescriptor() {
		return this;
	}

}
