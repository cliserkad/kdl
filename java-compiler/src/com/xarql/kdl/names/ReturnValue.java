package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;

public class ReturnValue implements StringOutput, ToName {
	public static final ReturnValue BOOLEAN_RETURN = returnValue(InternalName.BOOLEAN_IN);
	public static final ReturnValue INT_RETURN = returnValue(InternalName.INT_IN);
	public static final ReturnValue STRING_RETURN = returnValue(InternalName.STRING_IN);

	public static final ReturnValue VOID = new ReturnValue(null);
	public static final char        VOID_REP    = 'V';

	public final InternalObjectName returnType;

	private ReturnValue(InternalObjectName returnType) {
		this.returnType = returnType;
	}

	public static ReturnValue returnValue(InternalObjectName returnType) {
		if(returnType == null)
			return VOID;
		if(returnType.isBaseType()) {
			switch(returnType.toBaseType()) {
				case BOOLEAN:
					if(BOOLEAN_RETURN == null)
						return new ReturnValue(BaseType.BOOLEAN.toInternalObjectName());
					return BOOLEAN_RETURN;
				case INT:
					if(INT_RETURN == null)
						return new ReturnValue(BaseType.INT.toInternalObjectName());
					return INT_RETURN;
				case STRING:
					if(STRING_RETURN == null)
						return new ReturnValue(BaseType.STRING.toInternalObjectName());
					return STRING_RETURN;
			}
		}
		return new ReturnValue(returnType);
	}

	public static ReturnValue returnValue(InternalName returnType) {
		return returnValue(returnType.toInternalObjectName());
	}

	public static ReturnValue returnValue(Class<?> clazz) {
		return returnValue(new InternalObjectName(clazz));
	}

	public static ReturnValue nonNull(ReturnValue returnValue) {
		if(returnValue == null)
			return VOID;
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

	@Override
	public InternalName toInternalName() {
		if(returnType == null)
			return new InternalName();
		else
			return returnType.toInternalName();
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return returnType;
	}

	@Override
	public boolean isBaseType() {
		return  returnType.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return returnType.toBaseType();
	}
}
