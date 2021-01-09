package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;

public class Variable extends Details implements Assignable, Member, CommonText {

	public static final boolean DEFAULT_MUTABLE = false;

	public final int localIndex;

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
		} else
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
	public Variable push(final Actor visitor) throws UnimplementedException {
		if (type.isBaseType() && !type.isArray()) {
			switch (type.toBaseType()) {
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
		} else
			visitor.visitVarInsn(ALOAD, localIndex);
		return this;
	}

	@Override
	public Assignable assign(final InternalName incomingType, final Actor actor) throws Exception {
		if(!mutable && isInit()) {
			throw new IllegalArgumentException(this + " is not mutable and has been set.");
		} else if(!this.type.toInternalName().compatibleWith(this.type))
			throw new IncompatibleTypeException(incomingType + INCOMPATIBLE + this);
		else {
			if(this.type.isBaseType()) {
				// convert integer to long
				if(toBaseType() == BaseType.LONG && incomingType.toBaseType() == BaseType.INT)
					actor.visitInsn(I2L);
				// convert float to double
				if(toBaseType() == BaseType.DOUBLE && incomingType.toBaseType() == BaseType.FLOAT)
					actor.visitInsn(F2D);
			}

			if(isBaseType()) {
				switch(toBaseType()) {
					case BOOLEAN:
					case BYTE:
					case SHORT:
					case CHAR:
					case INT:
						actor.visitVarInsn(ISTORE, localIndex);
						break;
					case FLOAT:
						actor.visitVarInsn(FSTORE, localIndex);
						break;
					case LONG:
						actor.visitVarInsn(LSTORE, localIndex);
						break;
					case DOUBLE:
						actor.visitVarInsn(DSTORE, localIndex);
						break;
					case STRING:
						actor.visitVarInsn(ASTORE, localIndex);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}

				init();
			} else
				actor.visitVarInsn(ASTORE, localIndex);
		}
		return this;
	}

	@Override
	public Assignable assignDefault(Actor actor) throws Exception {
		if(isBaseType())
			assign(toBaseType().getDefaultValue().push(actor).toInternalName(), actor);
		else {
			actor.visitInsn(ACONST_NULL);
			actor.visitVarInsn(ASTORE, localIndex);
		}
		return this;
	}

	@Override
	public Details details() {
		return this;
	}

}
