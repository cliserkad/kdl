package com.xarql.kdl.names;

import com.xarql.kdl.JavaMethodDef;
import org.objectweb.asm.Opcodes;

import static com.xarql.kdl.BestList.list;

public interface CommonText extends Opcodes {
	// keywords
	String KEYWORD_BOOLEAN = "boolean";
	String KEYWORD_INT     = "int";
	String KEYWORD_STRING  = "string";
	String KEYWORD_TRUE    = "true";
	String KEYWORD_FALSE   = "false";
	String KEYWORD_CLASS   = "class";

	// Defaults for BaseTypes
	int     DEFAULT_INT     = 0;
	String  DEFAULT_STRING  = "";
	boolean DEFAULT_BOOLEAN = false;

	String                       EMPTY_STRING = "";

	JavaMethodDef      SB_APPEND     = new JavaMethodDef(new InternalName(StringBuilder.class), "append", list(new InternalName(String.class)), new ReturnValue(new InternalName(StringBuilder.class)), ACC_PUBLIC);
	JavaMethodDef      SB_TO_STRING  = new JavaMethodDef(new InternalName(StringBuilder.class), "toString", null, ReturnValue.STRING, ACC_PUBLIC);

	// handled by ExternalMethodRouter
	String PRINT   = "print";
	String PRINTLN = "println";
	String ERROR   = "error";

	ReturnValue VOID          = ReturnValue.VOID;
	String      DEFAULT       = "default";

	// Error messages
	String SWITCH_OPERATOR = "switch on Operator";
	String SWITCH_BASETYPE  = "switch on BaseType";
	String SWITCH_VALUETYPE = "switch on ValueType";
	String INCOMPATIBLE     = " has a type which is incompatible with the type of ";
	String BASETYPE_MISS    = "A BaseType was not accounted for";

}
