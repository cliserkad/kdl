package com.xarql.kdl.names;

import org.objectweb.asm.Opcodes;

public interface CommonText extends Opcodes {

	// keywords
	String KEYWORD_BOOLEAN = "boolean";
	String KEYWORD_INT = "int";
	String KEYWORD_STRING = "string";
	String KEYWORD_TRUE = "true";
	String KEYWORD_FALSE = "false";
	String KEYWORD_CLASS = "class";

	// Defaults for BaseTypes
	int DEFAULT_INT = 0;
	String DEFAULT_STRING = "";
	boolean DEFAULT_BOOLEAN = false;

	String EMPTY_STRING = "";

	// handled by ExternalMethodRouter
	String PRINT = "print";
	String PRINTLN = "println";
	String ERROR = "error";

	ReturnValue VOID = ReturnValue.VOID;
	String DEFAULT = "default";

	// Error messages
	String SWITCH_OPERATOR = "switch on Operator";
	String SWITCH_BASETYPE = "switch on BaseType";
	String SWITCH_VALUETYPE = "switch on ValueType";
	String INCOMPATIBLE = " has a type which is incompatible with the type of ";
	String BASETYPE_MISS = "A BaseType was not accounted for";

}
