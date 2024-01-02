package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;

public class Variable extends Details implements Assignable, CommonText {

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
		if(obj instanceof Variable other) {
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
		final int loadInstruction;
		if(isBaseType() && !isArray()) {
			loadInstruction = switch(type.toBaseType()) {
				case BOOLEAN, BYTE, SHORT, CHAR, INT -> ILOAD;
				case FLOAT -> FLOAD;
				case LONG -> LLOAD;
				case DOUBLE -> DLOAD;
				case STRING -> ALOAD;
			};
		} else {
			loadInstruction = ALOAD;
		}
		visitor.visitVarInsn(loadInstruction, localIndex);
		return this;
	}

	@Override
	public InternalName pushType(final Actor visitor) throws Exception {
		return push(visitor).toInternalName();
	}

	@Override
	public Assignable assign(final InternalName incomingType, final Actor actor) throws Exception {
		if(!mutable && isInit()) {
			throw new IllegalArgumentException(this + " is not mutable and has been set.");
		} else if(!incomingType.compatibleWith(toInternalName())) {
			throw new IncompatibleTypeException(incomingType + INCOMPATIBLE + this);
		} else {
			final int storeInstruction;
			if(isBaseType()) {
				storeInstruction = switch(toBaseType()) {
					case BOOLEAN, BYTE, SHORT, CHAR, INT -> ISTORE;
					case FLOAT -> FSTORE;
					case LONG -> {
						// convert integer to long automatically
						if(incomingType.toBaseType() == BaseType.INT)
							actor.visitInsn(I2L);
						yield LSTORE;
					}
					case DOUBLE -> {
						// convert float to double automatically
						if(incomingType.toBaseType() == BaseType.FLOAT)
							actor.visitInsn(F2D);
						yield DSTORE;
					}
					case STRING -> ASTORE;
				};
				init();
			} else {
				storeInstruction = ASTORE;
			}
			actor.visitVarInsn(storeInstruction, localIndex);
			return this;
		}
	}

	@Override
	public Assignable assignDefault(Actor actor) throws Exception {
		if(isBaseType())
			assign(toBaseType().getDefaultValue().pushType(actor), actor);
		else {
			actor.visitInsn(ACONST_NULL);
			actor.visitVarInsn(ASTORE, localIndex);
		}
		return this;
	}

}
