package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.InternalName;

public abstract class BasePushable implements Pushable {

	@Override
	public abstract Pushable push(final Actor actor) throws Exception;

	@Override
	public InternalName pushType(final Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

}
