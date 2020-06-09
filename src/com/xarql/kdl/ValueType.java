package com.xarql.kdl;

public enum ValueType {
	LITERAL(Literal.class), CONSTANT(Constant.class), VARIABLE(LocalVariable.class), LIMBO(Limbo.class), POINTER(Pointer.class);

	Class rep;

	ValueType(Class<?> rep) {
		this.rep = rep;
	}

	public static boolean isValue(Class<?> rep) {
		return match(rep) != null;
	}

	public static ValueType match(Class<?> rep) {
		for(ValueType v : ValueType.values())
			if(v.rep.equals(rep))
				return v;
		return null;
	}


}
