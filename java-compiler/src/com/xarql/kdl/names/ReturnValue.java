package com.xarql.kdl.names;

import com.xarql.kdl.StringOutput;

public class ReturnValue implements StringOutput, ToName {
	public static final ReturnValue BOOLEAN = new ReturnValue(BaseType.BOOLEAN);
	public static final ReturnValue BYTE    = new ReturnValue(BaseType.BYTE);
	public static final ReturnValue SHORT   = new ReturnValue(BaseType.SHORT);
	public static final ReturnValue CHAR    = new ReturnValue(BaseType.CHAR);
	public static final ReturnValue INT     = new ReturnValue(BaseType.INT);
	public static final ReturnValue FLOAT   = new ReturnValue(BaseType.FLOAT);
	public static final ReturnValue LONG    = new ReturnValue(BaseType.LONG);
	public static final ReturnValue DOUBLE  = new ReturnValue(BaseType.DOUBLE);
	public static final ReturnValue STRING  = new ReturnValue(BaseType.STRING);

	public static final ReturnValue VOID = new ReturnValue();
	public static final char        VOID_REP    = 'V';

	public final InternalName returnType;

	public ReturnValue(Class<?> clazz) {
		this(new InternalName(clazz));
	}

	public ReturnValue(ToName internalName) {
		if(internalName == null)
			this.returnType = null;
		else
			this.returnType = internalName.toInternalName();
	}

	public ReturnValue() {
		returnType = null;
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
			return returnType.internalObjectName();
	}

	@Override
	public InternalName toInternalName() {
		if(returnType == null)
			return new InternalName();
		else
			return returnType.toInternalName();
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
