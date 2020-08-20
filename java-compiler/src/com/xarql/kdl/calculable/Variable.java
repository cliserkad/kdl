package com.xarql.kdl.calculable;

import com.xarql.kdl.Text;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.*;
import org.objectweb.asm.MethodVisitor;

import static com.xarql.kdl.names.BaseType.BOOLEAN;
import static com.xarql.kdl.names.BaseType.INT;

public class Variable implements Resolvable, CommonText {
	public final String             name;
	public final InternalObjectName type;
	public final int                localIndex;

	public Variable(final String name, final InternalObjectName type, final int localIndex) {
		this.name = Text.nonNull(name);
		this.type = InternalObjectName.checkNonNull(type);
		this.localIndex = localIndex;
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

	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}

	@Override
	public InternalName toInternalName() {
		return type.inName;
	}

	@Override
	public InternalObjectName toInternalObjectName() {
		return type;
	}

	public boolean isArray() {
		return toInternalObjectName().isArray();
	}

	@Override
	public Resolvable push(final MethodVisitor visitor) throws UnimplementedException {
		if(type.isBaseType()) {
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
					throw new IllegalArgumentException(SWITCH_BASETYPE);
			}
		}
		else
			visitor.visitVarInsn(ALOAD, localIndex);
		return this;
	}

	@Override
	public Resolvable calc(final MethodVisitor visitor) throws Exception {
		push(visitor);
		return this;
	}
}
