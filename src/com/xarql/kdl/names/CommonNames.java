package com.xarql.kdl.names;

import com.xarql.kdl.BestList;
import com.xarql.kdl.MethodDef;

public interface CommonNames {
	// keywords
	String KEYWORD_BOOLEAN = "boolean";
	String KEYWORD_INT     = "int";
	String KEYWORD_STRING  = "string";

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
}
