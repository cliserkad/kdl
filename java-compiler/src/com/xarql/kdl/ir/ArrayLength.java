package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.Type;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.TypeDescriptor;

/**
 * Represents the access of an array's length.
 */
public class ArrayLength implements Pushable, CommonText {

	public final Pushable array;

	public ArrayLength(final Pushable array) {
		this.array = array;
	}

	/**
	 * Pushes an int to the stack that is equal to the array's length
	 *
	 * @param actor any Actor
	 * @throws Exception unused
	 */
	@Override
	public ArrayLength push(Actor actor) throws Exception {
		array.push(actor);
		actor.visitInsn(ARRAYLENGTH);
		return this;
	}

	/**
	 * @return BaseType.INT
	 */
	@Override
	public Type toType() {
		return BaseType.INT.type;
	}

	/**
	 * @return true
	 */
	@Override
	public boolean isBaseType() {
		return true;
	}

	/**
	 * @return BaseType.INT
	 */
	@Override
	public BaseType toBaseType() {
		return BaseType.INT;
	}

	@Override
	public TypeDescriptor toTypeDescriptor() {
		return BaseType.INT.toTypeDescriptor();
	}

}
