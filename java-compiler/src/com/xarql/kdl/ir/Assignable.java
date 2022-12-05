package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.InternalName;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public interface Assignable extends Pushable {

	public Assignable assign(final InternalName incomingType, final Actor actor) throws Exception;

	public Assignable assignDefault(final Actor actor) throws Exception;

	public static Assignable parse(final kdl.AssignmentContext ctx, final Actor actor) {
		final kdl.AddressableContext address = ctx.addressable();

		if(address.ID().size() > 1) {
			StaticField field = null;
			for(int i = 0; i < address.ID().size(); i++) {
				if(field == null)
					field = actor.unit.fields().equivalentKey(new ObjectField(address.ID(i).getText(), null, false, actor.unit.getClazz()));
				else
					field = actor.unit.fields().equivalentKey(new ObjectField(address.ID(i).getText(), null, false, field));
			}
			return field;
		} else if(!address.ID().isEmpty()) {
			if(actor.unit.getCurrentScope().contains(address.ID(0).getText()))
				return actor.unit.getLocalVariable(address.ID(0).getText());
			else
				return actor.unit.fields().equivalentKey(new ObjectField(address.ID(0).getText(), null, false, actor.unit.getClazz()));
		} else {
			System.err.println("Assignable failed to parse anything\nFull Text: " + ctx.getText());
			return null;
		}
	}

}
