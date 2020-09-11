package com.xarql.kdl.ir;

import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

public abstract class DefaultPushable implements Pushable {

	@Override
	public abstract Pushable push(final MethodVisitor visitor) throws Exception;

	@Override
	public InternalName pushType(final MethodVisitor visitor) throws Exception {
		return push(visitor).toInternalName();
	}
}
