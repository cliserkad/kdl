package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.TypeDescriptor;

public interface Assignable extends Member {

	public Assignable assign(final TypeDescriptor incomingType, final Actor actor) throws Exception;

	public Assignable assignDefault(final Actor actor) throws Exception;

	public static Assignable parse(final kdl.AssignmentContext ctx, final Actor actor) throws SymbolResolutionException {



		return null;
	}

}
