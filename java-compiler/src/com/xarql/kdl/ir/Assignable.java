package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.InternalName;

public interface Assignable extends Pushable {

	public Assignable assign(final InternalName incomingType, final Actor actor) throws Exception;

	public Assignable assignDefault(final Actor actor) throws Exception;

	public static Assignable parse(final kdl.AssignmentContext ctx, final Actor actor) {
		if(ctx.VARNAME() != null) {
			return actor.unit.getLocalVariable(ctx.VARNAME().getText());
		} else {
			return null;
		}
	}

}
