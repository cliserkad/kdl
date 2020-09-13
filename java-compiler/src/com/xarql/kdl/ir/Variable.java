package com.xarql.kdl.ir;

import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

public class Variable extends Details implements Pushable, CommonText {
	public static final boolean DEFAULT_MUTABLE = false;

	public final int          localIndex;

	// track if it's been set
	private boolean init = false;

	public Variable(final String name, final InternalName type, final int localIndex, final boolean mutable) {
		super(name, type, mutable);
		if(type == null)
			throw new NullPointerException();
		this.localIndex = localIndex;
	}

	public Variable(final String name, final InternalName type, final int localIndex) {
		this(name, type, localIndex, DEFAULT_MUTABLE);
	}

	public boolean isInit() {
		return init;
	}

	public void init() {
		init = true;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Variable) {
			Variable other = (Variable) obj;
			return other.name.equals(name);
		}
		else
			return false;
	}

	@Override
	public String toString() {
		return "LocalVariable: " + name + " --> " + type + " @ " + localIndex;
	}

	public boolean isArray() {
		return type.isArray();
	}

	@Override
	public Variable push(final MethodVisitor visitor) throws UnimplementedException {
		if(type.isBaseType() && !type.isArray()) {
			switch(type.toBaseType()) {
				case BOOLEAN:
				case BYTE:
				case SHORT:
				case CHAR:
				case INT:
					visitor.visitVarInsn(ILOAD, localIndex);
					break;
				case FLOAT:
					visitor.visitVarInsn(FLOAD, localIndex);
					break;
				case LONG:
					visitor.visitVarInsn(LLOAD, localIndex);
					break;
				case DOUBLE:
					visitor.visitVarInsn(DLOAD, localIndex);
					break;
				case STRING:
					visitor.visitVarInsn(ALOAD, localIndex);
					break;
				default:
					throw new UnimplementedException(SWITCH_BASETYPE);
			}
		}
		else
			visitor.visitVarInsn(ALOAD, localIndex);
		return this;
	}

	@Override
	public InternalName pushType(final MethodVisitor visitor) throws Exception {
		return push(visitor).toInternalName();
	}

}
