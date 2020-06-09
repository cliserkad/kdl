package com.xarql.kdl.names;

import com.xarql.kdl.BestList;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.Operator;
import com.xarql.kdl.ValueType;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public interface CommonNames {
	// keywords
	String KEYWORD_BOOLEAN = "boolean";
	String KEYWORD_INT     = "int";
	String KEYWORD_STRING  = "string";

	// Defaults for BaseTypes
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

	// StringBuilder
	InternalName       STRING_BUILDER_IN    = internalName(StringBuilder.class);
	String             STRING_BUILDER_IN_S  = STRING_BUILDER_IN.stringOutput();
	InternalObjectName STRING_BUILDER_ION   = STRING_BUILDER_IN.object();
	String             STRING_BUILDER_ION_S = STRING_BUILDER_ION.stringOutput();
	ReturnValue        STRING_BUILDER_RV    = new ReturnValue(StringBuilder.class);
	JavaMethodDef      SB_APPEND            = new JavaMethodDef(STRING_BUILDER_IN, "append", list(STRING_ION), STRING_BUILDER_RV, ACC_PUBLIC);
	JavaMethodDef      SB_TO_STRING         = new JavaMethodDef(STRING_BUILDER_IN, "toString", null, STRING_RV, ACC_PUBLIC);

	// handled by ExternalMethodRouter
	String      PRINT         = "print";
	String      PRINTLN       = "println";
	String      NO_PARAM_VOID = "()V";
	ReturnValue VOID          = ReturnValue.VOID_RETURN;
	String      VOID_S        = VOID.stringOutput();
	String      INIT          = JavaMethodDef.INIT;
	String      DEFAULT       = "default";

	// BaseType
	BaseType     INT        = BaseType.INT;
	BaseType     BOOLEAN    = BaseType.BOOLEAN;
	BaseType     STRING     = BaseType.STRING;
	InternalName INT_IN     = InternalName.INT_IN;
	InternalName BOOLEAN_IN = InternalName.BOOLEAN_IN;

	// Error messages
	String SWITCH_OPERAOTR  = "switch on Operator";
	String SWITCH_BASETYPE  = "switch on BaseType";
	String SWITCH_VALUETYPE = "switch on ValueType";
	String INCOMPATIBLE     = " has a type which is incompatible with the type of ";
	String BASETYPE_MISS    = "A BaseType was not accounted for";

	// operators
	Operator PLUS     = Operator.PLUS;
	Operator MINUS    = Operator.MINUS;
	Operator MULTIPLY = Operator.MULTIPLY;
	Operator DIVIDE   = Operator.DIVIDE;
	Operator MODULUS  = Operator.MODULUS;

	// ValueType
	ValueType LITERAL      = ValueType.LITERAL;
	ValueType CONSTANT     = ValueType.CONSTANT;
	ValueType VARIABLE     = ValueType.VARIABLE;
	ValueType LIMBO        = ValueType.LIMBO;
	ValueType POINTER      = ValueType.POINTER;
	ValueType ARRAY_ACCESS = ValueType.ARRAY_ACCESS;

}
