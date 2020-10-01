package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.InternalName;

public interface Assignable extends Pushable {

	public Assignable assign(final InternalName incomingType, final Actor actor) throws Exception;

	public Assignable assignDefault(final Actor actor) throws Exception;

	public static Assignable parse(final kdl.AssignmentContext ctx, final Actor actor) {
		if(ctx.field() != null) {
			Field field = null;
			for(int i = 0; i < ctx.field().VARNAME().size(); i++) {
				if(field == null)
					field = new Field(ctx.field().VARNAME(i).getText(), null, false, actor.unit.getClazz());
				else
					field = new Field(ctx.field().VARNAME(i).getText(), null, false, field);
			}
			return field;
		} else if(ctx.VARNAME() != null) {
			if(actor.unit.getCurrentScope().contains(ctx.VARNAME().getText()))
				return actor.unit.getLocalVariable(ctx.VARNAME().getText());
			else
				return actor.unit.fields.equivalentKey(new Field(ctx.VARNAME().getText(), null, false, actor.unit.getClazz()));
		} else {
			return null;
		}
	}

}
