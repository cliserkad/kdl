package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.Opcodes;

public class StaticField extends Details implements Pushable {

	public final InternalName owner;

	public StaticField(Details details, InternalName owner) {
		super(details);
		if(owner == null)
			throw new NullPointerException();
		this.owner = owner;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		actor.visitFieldInsn(Opcodes.GETSTATIC, owner.nameString(), name, type.objectString());
		return this;
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

}
