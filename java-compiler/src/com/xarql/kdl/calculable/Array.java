package com.xarql.kdl.calculable;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToBaseType;

public class Array implements ToBaseType {
	public static final int MIN_DIMENSIONS = 1;
	public static final int MAX_DIMENSIONS = 255;


	public InternalName type;
	public int          dimensions;

	public Array(InternalName ion) {
		this(ion, ion.arrayDimensions);
	}

	public Array(InternalName internalName, int dimensions) {
		this.type = internalName;
		this.dimensions = checkDimensions(dimensions);
	}

	public static int checkDimensions(int dims) {
		if(dims > MAX_DIMENSIONS || dims < MIN_DIMENSIONS)
			throw new IllegalArgumentException("An Array may not have " + dims + " dimensions. The acceptable range is [" + MIN_DIMENSIONS + "," + MAX_DIMENSIONS + "].");
		else
			return dims;
	}

	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}
}
