package com.xarql.kdl.names;

import com.xarql.kdl.BestList;
import com.xarql.kdl.MethodDef;

public interface CommonNames {
	String BASETYPE_INT    = "int";
	String BASETYPE_STRING = "string";

	InternalName                 STRING_IN    = new InternalName(String.class);
	String                       STRING_IN_S  = STRING_IN.toString();
	InternalObjectName           STRING_ION   = new InternalObjectName(STRING_IN);
	String                       STRING_ION_S = STRING_ION.toString();
	BestList<InternalObjectName> STRING_PARAM = new BestList<>(STRING_ION);
	ReturnValue                  STRING_RV    = new ReturnValue(STRING_ION);
	ReturnValue                  VOID         = ReturnValue.VOID_RETURN;
	String                       PRINT        = "print";
	String                       PRINTLN      = "println";
	String                       DEFAULT      = "default";
	InternalName                 INT          = InternalName.INT;
	InternalName                 BOOLEAN      = InternalName.BOOLEAN;
	ReturnValue                  VOID_RETURN  = ReturnValue.VOID_RETURN;
	String                       INIT         = MethodDef.INIT;
}
