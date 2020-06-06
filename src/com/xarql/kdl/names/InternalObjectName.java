package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;

public class InternalObjectName implements StringOutput {
	public static final String OBJECT_SUFFIX            = ";";
	public static final String OBJECT_PREFIX            = "L";
	public static final String ARRAY_PREFIX             = "[";
	public static final int    DEFAULT_ARRAY_DIMENSIONS = 0;

	final InternalName internalName;
	final int          arrayDimensions;

	public InternalObjectName(InternalName internalName, int arrayDimensions) {
		this.internalName = internalName;
		this.arrayDimensions = arrayDimensions;
	}

	public InternalObjectName(Class<?> clazz, int arrayDimensions) {
		this(new InternalName(clazz), arrayDimensions);
	}

	public InternalObjectName(InternalName internalName) {
		this(internalName, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalObjectName(Class<?> clazz) {
		this(new InternalName(clazz));
	}

	public static InternalObjectName checkNonNull(InternalObjectName name) {
		if(name == null)
			throw new IllegalArgumentException("InternalObjectName instance may not be null");
		return name;
	}

	private String objectInstance() {
		return OBJECT_PREFIX + internalName.stringOutput() + OBJECT_SUFFIX;
	}

	@Override
	public String stringOutput() {
		if(internalName.isBaseType())
			return internalName.stringOutput();
		else if(arrayDimensions == 0)
			return objectInstance();
		else {
			String dims = "";
			for(int i = 0; i < arrayDimensions; i++)
				dims += ARRAY_PREFIX;
			return dims + objectInstance();
		}
	}

	@Override
	public String toString() {
		return stringOutput();
	}
}
