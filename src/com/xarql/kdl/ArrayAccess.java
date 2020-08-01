package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;
import jdk.internal.org.objectweb.asm.Opcodes;

public class ArrayAccess implements Resolvable, CommonNames, Opcodes {
	public final Variable   array;
	public final Resolvable index;

	public ArrayAccess(final Variable array, final Resolvable index) {
		this.array = array;
		this.index = index;
	}

	@Override
	public void push(LinedMethodVisitor lmv) throws Exception {
		lmv.visitVarInsn(ALOAD, array.localIndex);

		// throw error if value within [ ] isn't an int
		if(index.toBaseType() != INT)
			throw new IncompatibleTypeException("The input for an array access must be an integer");
		else
			index.push(lmv);

		if(array.type.isBaseType()) {
			switch(array.type.toBaseType()) {
				case INT:
				case BOOLEAN:
					lmv.visitInsn(IALOAD);
				case STRING:
					lmv.visitInsn(AALOAD);
			}
		}
		else
			lmv.visitInsn(AALOAD);
	}

	@Override
	public InternalName toInternalName() {
		return null;
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return null;
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
