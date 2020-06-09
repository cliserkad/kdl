package com.xarql.kdl.names;

import com.xarql.kdl.BestList;
import com.xarql.kdl.MethodDef;
import com.xarql.kdl.Operator;
import com.xarql.kdl.ValueType;

public interface CommonNames {
	// keywords
	String KEYWORD_BOOLEAN = "boolean";
	String KEYWORD_INT     = "int";
	String KEYWORD_STRING  = "string";

	int     DEFAULT_INT     = 0;
	String  DEFAULT_STRING  = "";
	boolean DEFAULT_BOOLEAN = false;

	// string related
	InternalName                 STRING_IN    = InternalName.STRING_IN;
	String                       STRING_IN_S  = STRING_IN.toString();
	InternalObjectName           STRING_ION   = new InternalObjectName(STRING_IN);
	String                       STRING_ION_S = STRING_ION.toString();
	BestList<InternalObjectName> STRING_PARAM = new BestList<>(STRING_ION);
	ReturnValue                  STRING_RV    = new ReturnValue(STRING_ION);

	// handled by ExternalMethodRouter
	String PRINT   = "print";
	String PRINTLN = "println";

	ReturnValue VOID = ReturnValue.VOID_RETURN;
	String      INIT = MethodDef.INIT;

	String DEFAULT = "default";

	BaseType INT     = BaseType.INT;
	BaseType BOOLEAN = BaseType.BOOLEAN;
	BaseType STRING  = BaseType.STRING;

	InternalName INT_IN     = InternalName.INT_IN;
	InternalName BOOLEAN_IN = InternalName.BOOLEAN_IN;

	String SWITCH_OPERAOTR = "switch on Operator";
	String SWITCH_BASETYPE = "switch on BaseType";
	String INCOMPATIBLE    = " has a type which is incompatible with the type of ";
	String BASETYPE_MISS   = "A BaseType was not accounted for";

	// operators
	Operator PLUS     = Operator.PLUS;
	Operator MINUS    = Operator.MINUS;
	Operator MULTIPLY = Operator.MULTIPLY;
	Operator DIVIDE   = Operator.DIVIDE;
	Operator MODULUS  = Operator.MODULUS;

	// ValueType
	ValueType LITERAL  = ValueType.LITERAL;
	ValueType CONSTANT = ValueType.CONSTANT;
	ValueType VARIABLE = ValueType.VARIABLE;
	ValueType LIMBO    = ValueType.LIMBO;
	ValueType POINTER  = ValueType.POINTER;

}
