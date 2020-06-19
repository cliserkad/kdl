package com.xarql.kdl.names;

import com.xarql.kdl.Array;
import com.xarql.kdl.StringOutput;

import static com.xarql.kdl.names.InternalName.internalName;

public class InternalObjectName implements StringOutput, ToName {
	public static final String OBJECT_SUFFIX            = ";";
	public static final String OBJECT_PREFIX            = "L";
	public static final String ARRAY_PREFIX             = "[";
	public static final int    DEFAULT_ARRAY_DIMENSIONS = 0;

	public final InternalName inName;
	public final int          arrayDimensions;

	public InternalObjectName(InternalName inName, int arrayDimensions) {
		this.inName = inName;
		this.arrayDimensions = arrayDimensions;
	}

	public InternalObjectName(Class<?> clazz, int arrayDimensions) {
		this(internalName(clazz), arrayDimensions);
	}

	public InternalObjectName(InternalName inName) {
		this(inName, DEFAULT_ARRAY_DIMENSIONS);
	}

	public InternalObjectName(Class<?> clazz) {
		this(internalName(clazz));
	}

	public static InternalObjectName checkNonNull(InternalObjectName name) {
		if(name == null)
			throw new IllegalArgumentException("InternalObjectName instance may not be null");
		return name;
	}

	private String objectInstance() {
		return OBJECT_PREFIX + inName.stringOutput() + OBJECT_SUFFIX;
	}

	@Override
	public String stringOutput() {
		String dims = "";
		for(int i = 0; i < arrayDimensions; i++)
			dims += ARRAY_PREFIX;

		if(inName.isBaseType() && inName.toBaseType() != BaseType.STRING)
			return dims + inName.stringOutput();
		else
			return dims + objectInstance();
	}

	@Override
	public String toString() {
		return stringOutput();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof StringOutput) {
			StringOutput so = (StringOutput) o;
			return so.stringOutput().equals(stringOutput());
		}
		return false;
	}

	@Override
	public boolean isBaseType() {
		return inName.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return inName.base;
	}

	public boolean isArray() {
		return arrayDimensions >= Array.MIN_DIMENSIONS;
	}

	public Array toArray() {
		return new Array(inName, arrayDimensions);
	}

	@Override
	public InternalName toInternalName() {
		return inName;
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return this;
	}
}
