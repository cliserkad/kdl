package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;

public class Null implements Pushable, CommonText {

	@Override
	public Pushable push(final Actor visitor) throws Exception {
		visitor.visitInsn(ACONST_NULL);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return InternalName.OBJECT;
	}

	@Override
	public boolean isBaseType() {
		return false;
	}

	@Override
	public BaseType toBaseType() {
		return null;
	}

}
