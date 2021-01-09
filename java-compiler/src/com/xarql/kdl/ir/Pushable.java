package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.names.ToType;
import com.xarql.kdl.names.ToTypeDescriptor;

public interface Pushable extends ToTypeDescriptor {

	/**
	 * Pushes this value on to the stack. Executes sub-pushes and instructions if
	 * needed.
	 *
	 * @param actor any Actor
	 * @return instance of implementing class; whatever "this" is
	 * @throws Exception if pushing is impossible
	 */
	public Pushable push(final Actor actor) throws Exception;

}
