package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.Opcodes;

public class ObjectField extends StaticField implements Assignable {

	public static final String NO_OWNER = "owner of a field must be known when pushed";

	public final Pushable owner;

	public ObjectField(Details details, Pushable owner) {
		super(details, owner.toInternalName());
		this.owner = owner;
	}

	public ObjectField(String name, InternalName type, boolean mutable, Pushable owner) {
		this(new Details(name, type, mutable), owner);
	}

	public ObjectField(Details details, InternalName ownerType) {
		super(details, ownerType);
		this.owner = null;
	}

	@Override
	public ObjectField assign(InternalName incomingType, Actor actor) throws Exception {
		if(owner == null)
			throw new NullPointerException(NO_OWNER);
		final InternalName ownerType = owner.pushType(actor);
		actor.visitInsn(Opcodes.SWAP);
		actor.visitFieldInsn(Opcodes.PUTFIELD, ownerType.nameString(), name, type.objectString());
		return this;
	}

	@Override
	public ObjectField assignDefault(Actor actor) throws Exception {
		final InternalName incomingType = type.toBaseType().defaultValue.pushType(actor);
		assign(incomingType, actor);
		return this;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		if(owner == null)
			throw new NullPointerException(NO_OWNER);
		final InternalName ownerType = owner.pushType(actor);
		actor.visitFieldInsn(Opcodes.GETFIELD, ownerType.nameString(), name, type.objectString());
		return this;
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

}
