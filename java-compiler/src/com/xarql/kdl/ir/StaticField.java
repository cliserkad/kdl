package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.Opcodes;

public class StaticField extends Details implements Assignable {

	public final InternalName ownerType;

	public StaticField(Details details, InternalName ownerType) {
		super(details);
		this.ownerType = ownerType;
	}

	public StaticField(String name, InternalName ownerType) {
		this(new Details(name), ownerType);
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		if(type == null) {
			StaticField proper = actor.unit.fields().equivalentKey(this);
			return proper.push(actor);
		} else {
			actor.visitFieldInsn(Opcodes.GETSTATIC, ownerType.nameString(), name, type.objectString());
			return this;
		}
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

	@Override
	public StaticField assign(InternalName incomingType, Actor actor) throws Exception {
		actor.visitFieldInsn(Opcodes.PUTSTATIC, ownerType.nameString(), name, type.objectString());
		return this;
	}

	@Override
	public StaticField assignDefault(Actor actor) throws Exception {
		if(isBaseType())
			toBaseType().defaultValue.push(actor);
		else
			actor.visitInsn(Opcodes.ACONST_NULL);
		return assign(type, actor);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof StaticField) {
			final StaticField other = (StaticField) object;
			return other.ownerType.equals(ownerType) && other.name.equals(name);
		} else
			return false;
	}

	public String toString() {
		return ownerType.nameString() + " " + super.toString();
	}

}
