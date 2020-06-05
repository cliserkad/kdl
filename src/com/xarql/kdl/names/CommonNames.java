package com.xarql.kdl.names;

import com.xarql.kdl.BestList;

public interface CommonNames {
	public static final InternalName                 STRING_IN    = new InternalName(String.class);
	public static final String                       STRING_IN_S  = STRING_IN.toString();
	public static final InternalObjectName           STRING_ION   = new InternalObjectName(STRING_IN);
	public static final String                       STRING_ION_S = STRING_ION.toString();
	public static final BestList<InternalObjectName> STRING_PARAM = new BestList<>(STRING_ION);
	public static final ReturnValue                  STRING_RV    = new ReturnValue(STRING_ION);
	public static final ReturnValue                  VOID         = ReturnValue.VOID_RETURN;
	public static final String                       PRINT        = "print";
	public static final String                       PRINTLN      = "println";
}
