package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.Opcodes;

public class Field extends Details implements Assignable {

	public final Pushable owner;

	public Field(Details details, Pushable owner) {
		super(details);
		this.owner = owner;
	}

	public Field(String name, InternalName type, boolean mutable, Pushable owner) {
		super(name, type, mutable);
		this.owner = owner;
	}

	@Override
	public Field assign(InternalName incomingType, Actor actor) throws Exception {
		final InternalName ownerType = owner.pushType(actor);
		actor.visitInsn(Opcodes.SWAP);
		actor.visitFieldInsn(Opcodes.PUTFIELD, ownerType.nameString(), name, type.objectString());
		return this;
	}

	@Override
	public Field assignDefault(Actor actor) throws Exception {
		final InternalName incomingType = type.toBaseType().defaultValue.pushType(actor);
		assign(incomingType, actor);
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Field) {
			final Field other = (Field) object;
			return other.owner.equals(owner) && other.name.equals(name);
		} else
			return false;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		final InternalName ownerType = owner.pushType(actor);
		actor.visitFieldInsn(Opcodes.GETFIELD, ownerType.nameString(), name, type.objectString());
		return this;
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

}
