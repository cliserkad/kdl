package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.Opcodes;

public class Constant extends BasePushable {

	public final String name;
	public final InternalName type;
	public final InternalName owner;

	public Constant(final String name, final InternalName type, final InternalName owner) {
		if(name == null || name.isEmpty())
			throw new IllegalArgumentException("Constant name may not be empty");
		this.name = name;
		if(type == null)
			throw new NullPointerException();
		this.type = type;
		if(owner == null)
			throw new NullPointerException();
		this.owner = owner;
	}

	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}

	@Override
	public String toString() {
		return "Constant: " + name + " --> " + type;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Constant) {
			Constant other = (Constant) o;
			return other.name.equals(name) && other.owner.equals(owner);
		}
		return false;
	}

	@Override
	public InternalName toInternalName() {
		return type;
	}

	@Override
	public Pushable push(final Actor actor) {
		actor.visitFieldInsn(Opcodes.GETSTATIC, owner.nameString(), name, type.objectString());
		return this;
	}

}
