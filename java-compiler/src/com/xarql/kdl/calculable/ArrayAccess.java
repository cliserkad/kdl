package com.xarql.kdl.calculable;

import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

import static com.xarql.kdl.names.BaseType.INT;

/**
 * Represents the access of an array's element
 */
public class ArrayAccess extends DefaultResolvable implements CommonText {
	public final Variable   array;
	public final Resolvable index;

	public ArrayAccess(final Variable array, final Resolvable index) {
		this.array = array;
		this.index = index;
	}

	@Override
	public Resolvable push(MethodVisitor lmv) throws Exception {
		lmv.visitVarInsn(ALOAD, array.localIndex);

		// throw error if value within [ ] isn't an int
		if(index.toBaseType().ordinal() > INT.ordinal())
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

		return this;
	}

	@Override
	public InternalName toInternalName() {
		return array.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return array.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return array.toBaseType();
	}

	@Override
	public String toString() {
		return "ArrayAccess --> {\n\t" + array + "\n\t" + index + "\n}";
	}
}
