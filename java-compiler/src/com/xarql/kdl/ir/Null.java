package com.xarql.kdl.ir;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

public class Null implements Resolvable, CommonText {
	@Override
	public Resolvable push(final MethodVisitor visitor) throws Exception {
		visitor.visitInsn(ACONST_NULL);
		return this;
	}

	@Override
	public ToName calc(final MethodVisitor visitor) throws Exception {
		push(visitor);
		return toInternalName();
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
