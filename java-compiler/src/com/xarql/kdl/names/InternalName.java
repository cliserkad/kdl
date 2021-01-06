package com.xarql.kdl.names;

import com.xarql.kdl.Path;
import com.xarql.kdl.PlaceHolder;

public class InternalName implements ToName, CommonText {

	public static final InternalName BOOLEAN = new InternalName(BaseType.BOOLEAN.rep);
	public static final InternalName BYTE = new InternalName(BaseType.BYTE.rep);
	public static final InternalName SHORT = new InternalName(BaseType.SHORT.rep);
	public static final InternalName CHAR = new InternalName(BaseType.CHAR.rep);
	public static final InternalName INT = new InternalName(BaseType.INT.rep);
	public static final InternalName FLOAT = new InternalName(BaseType.FLOAT.rep);
	public static final InternalName LONG = new InternalName(BaseType.LONG.rep);
	public static final InternalName DOUBLE = new InternalName(BaseType.DOUBLE.rep);
	public static final InternalName STRING = new InternalName(BaseType.STRING.rep);
	public static final InternalName PLACEHOLDER = new InternalName(PlaceHolder.class);

	public static final InternalName STRING_BUILDER = new InternalName(StringBuilder.class);
	public static final InternalName INT_WRAPPER = new InternalName(Integer.class);
	public static final InternalName BOOLEAN_WRAPPER = new InternalName(Boolean.class);

	public static final InternalName OBJECT = new InternalName(Object.class);
	public static final InternalName ARRAY = new InternalName(Object.class, 1);

	public static final Path DEFAULT_PATH = null;
	public static final String OBJECT_SUFFIX = ";";
	public static final String OBJECT_PREFIX = "L";
	public static final String ARRAY_PREFIX = "[";
	public static final int DEFAULT_ARRAY_DIMENSIONS = 0;
	public static final int MIN_DIMENSIONS = 0;
	public static final int MAX_DIMENSIONS = 255;

	public final Path path;
	public final int arrayDimensions;

	public InternalName() {
		path = DEFAULT_PATH;
		arrayDimensions = DEFAULT_ARRAY_DIMENSIONS;
	}

	public InternalName(final Path path) {
		this(path, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final Path path, final int arrayDimensions) {
		if(path == null || path.size() == 0)
			throw new IllegalArgumentException("StringPath path may not be null");
		if(arrayDimensions < MIN_DIMENSIONS || arrayDimensions > MAX_DIMENSIONS)
			throw new IllegalArgumentException("int arrayDimensions must be within " + MIN_DIMENSIONS + " & " + MAX_DIMENSIONS);
		this.path = path;
		this.arrayDimensions = arrayDimensions;
	}

	public InternalName(final Class<?> c, final int arrayDimensions) {
		this(Path.forClass(c), arrayDimensions);
	}

	public InternalName(final Class<?> c) {
		this(c, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalName(final BaseType baseType, final int arrayDimensions) {
		this.path = baseType.rep;
		this.arrayDimensions = arrayDimensions;
	}

	public Object defaultValue() {
		if(isBaseType())
			return toBaseType().defaultValue;
		else
			return null;
	}

	public boolean isVoid() {
		return path == null;
	}

	@Override
	public boolean isBaseType() {
		return BaseType.isBaseType(path);
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.matchPath(path);
	}

	/**
	 * Provides the qualified name, surrounded with object markers.
	 * If this InternalName represents a BaseType which is not string,
	 * then qualifiedName() is returned.
	 * Ex: Ljava/lang/String;
	 */
	private String objectName() {
		if(isBaseType() && toBaseType() != BaseType.STRING)
			return qualifiedName();
		else
			return OBJECT_PREFIX + qualifiedName() + OBJECT_SUFFIX;
	}

	/**
	 * Provides the objectName, prefixed with arrayDimensions amount
	 * of array dimension markers
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
		if(path != null)
			return path.toString();
		else
			return "" + ReturnValue.VOID_REP;
	}

	/**
	 * Provides the type name of this InternalName.
	 * Ex: String
	 */
	public String name() {
		if(qualifiedName().contains("/"))
			return qualifiedName().substring(0, qualifiedName().indexOf('/'));
		else
			return qualifiedName();
	}

	@Override
	public String toString() {
		return arrayName();
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		} else if(o instanceof InternalName) {
			InternalName in = (InternalName) o;
			return in.path.equals(path);
		} else if(o instanceof BaseType) {
			BaseType base = (BaseType) o;
			return base.toInternalName().equals(this);
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

	public InternalName toArray(final int dimensions) {
		if(isVoid())
			throw new IllegalArgumentException("This InternalName represents void, which is not a valid array type.");
		else {
			return new InternalName(path, dimensions);
		}
	}

	public InternalName withoutArray() {
		if(isArray()) {
			return new InternalName(path);
		} else {
			return this;
		}
	}

}
