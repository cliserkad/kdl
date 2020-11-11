package com.xarql.kdl;

import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.Opcodes;

import static com.xarql.kdl.Text.nonNull;

public class CustomClass implements ToName, Pushable {
	public static final char INTERNAL_SEPARATOR = '/';
	public static final char SOURCE_SEPARATOR = '.';

	public final String pkg;
	public final String name;

	public CustomClass(String pkg, String name) {
		this.pkg = nonNull(pkg);
		if(name == null)
			throw new NullPointerException("A CustomClass' name must not be null");
		if(name.trim().isEmpty())
			throw new IllegalArgumentException("A CustomClass' name must not be empty");
		this.name = name;
	}

	public CustomClass(String name) {
		this(null, name);
	}

	public String qualifiedName() {
		return fullName().replace(SOURCE_SEPARATOR, INTERNAL_SEPARATOR);
	}

	public String fullName() {
		if(!pkg.isEmpty())
			return pkg + SOURCE_SEPARATOR + name;
		else
			return name;
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
