package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.TypeDescriptor;
import org.objectweb.asm.Opcodes;

public class ObjectField extends StaticField implements Assignable {

	public static final String NO_OWNER = "owner of a field must be known when pushed";

	public final Pushable owner;

	public ObjectField(Details details, Pushable owner) {
		super(details, owner.toType());
		this.owner = owner;
	}

	public ObjectField(String name, Pushable owner) {
		this(name, null, Variable.DEFAULT_MUTABLE, owner);
	}

	public ObjectField(String name, TypeDescriptor type, boolean mutable, Pushable owner) {
		this(new Details(name, type, mutable), owner);
	}

	public ObjectField(Details details, TypeDescriptor ownerType) {
		super(details, ownerType);
		this.owner = null;
	}

	@Override
	public ObjectField assign(TypeDescriptor incomingType, Actor actor) throws Exception {
		if(owner == null)
			throw new NullPointerException(NO_OWNER);
		final TypeDescriptor ownerType = owner.push(actor).toType();
		actor.visitInsn(Opcodes.SWAP);
		actor.visitFieldInsn(Opcodes.PUTFIELD, ownerType.qualifiedName(), name.text, descriptor.arrayName());
		return this;
	}

	@Override
	public ObjectField assignDefault(Actor actor) throws Exception {
		final TypeDescriptor incomingType = descriptor.toBaseType().getDefaultValue().push(actor).toType();
		assign(incomingType, actor);
		return this;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		if(owner == null)
			throw new NullPointerException(NO_OWNER);
		final TypeDescriptor ownerType = owner.push(actor).toType();
		actor.visitFieldInsn(Opcodes.GETFIELD, ownerType.qualifiedName(), name.text, descriptor.arrayName());
		return this;
	}

}
