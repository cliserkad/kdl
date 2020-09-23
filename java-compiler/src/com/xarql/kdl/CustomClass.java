package com.xarql.kdl;

import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.Opcodes;

public class CustomClass implements ToName, Pushable {

	public final String pkg;
	public final String name;

	public CustomClass(String pkg, String name) {
		this.pkg = pkg;
		this.name = name;
	}

	public CustomClass(String name) {
		this(null, name);
	}

	@Override
	public String toString() {
		return toInternalName().objectString();
	}

	@Override
	public InternalName toInternalName() {
		return new InternalName(this);
	}

	@Override
	public boolean isBaseType() {
		return false;
	}

	@Override
	public BaseType toBaseType() {
		return null;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		// attempt to load "this"
		actor.visitVarInsn(Opcodes.ALOAD, 0);
		return this;
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

}
