package com.xarql.kdl.ir;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

/**
 * Represents the access of an array's length.
 */
public class ArrayLength extends BasePushable implements CommonText {

	public final Variable array;

	public ArrayLength(final Variable array) {
		this.array = array;
	}

	/**
	 * Pushes an int to the stack that is equal to the array's length
	 * 
	 * @param visitor any MethodVisitor
	 * @throws Exception unused
	 */
	@Override
	public Pushable push(MethodVisitor visitor) throws Exception {
		array.push(visitor);
		visitor.visitInsn(ARRAYLENGTH);
		return this;
	}

	/**
	 * @return InternalName.INT
	 */
	@Override
	public InternalName toInternalName() {
		return InternalName.INT;
	}

	/**
	 * Determines if this will provide a base type
	 * 
	 * @return true
	 */
	@Override
	public boolean isBaseType() {
		return true;
	}

	/**
	 * An array's length is always an INT
	 * 
	 * @return BaseType.INT
	 */
	@Override
	public BaseType toBaseType() {
		return BaseType.INT;
	}

}
