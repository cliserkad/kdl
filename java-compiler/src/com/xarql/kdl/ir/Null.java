package com.xarql.kdl.ir;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

public class Null extends BasePushable implements CommonText {

	@Override
	public Pushable push(final MethodVisitor visitor) throws Exception {
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
