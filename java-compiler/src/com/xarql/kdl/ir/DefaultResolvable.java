package com.xarql.kdl.ir;

import org.objectweb.asm.MethodVisitor;

public abstract class DefaultResolvable implements Resolvable {

	@Override
	public Resolvable calc(final MethodVisitor visitor) throws Exception {
		push(visitor);
		return this;
	}

	@Override
	public abstract Resolvable push(MethodVisitor visitor) throws Exception;
}
