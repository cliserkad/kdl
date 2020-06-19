package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;

public class ReturnValue implements StringOutput {
	public static final ReturnValue BOOLEAN_RETURN = new ReturnValue(InternalName.BOOLEAN_IN);

	public static final ReturnValue VOID_RETURN = new ReturnValue();
	public static final char        VOID_REP    = 'V';

	public final InternalObjectName returnType;

	public ReturnValue(InternalObjectName returnType) {
		this.returnType = returnType;
	}

	public ReturnValue(InternalName returnType) {
		this(new InternalObjectName(returnType));
	}

	public ReturnValue(Class<?> clazz) {
		this(new InternalObjectName(clazz));
	}

	public ReturnValue() {
		this((InternalObjectName) null);
	}

	public static ReturnValue nonNull(ReturnValue returnValue) {
		if(returnValue == null)
			return VOID_RETURN;
		else
			return returnValue;
	}

	public boolean isVoid() {
		return returnType == null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null && isVoid())
			return true;
		else if(obj instanceof ReturnValue) {
			ReturnValue rv = (ReturnValue) obj;
			if(isVoid()) {
				return rv.isVoid();
			}
			else
				return rv.returnType.equals(returnType);
		}
		else
			return false;
	}

	@Override
	public String stringOutput() {
		if(returnType == null)
			return "" + VOID_REP;
		else
			return returnType.stringOutput();
	}
}
