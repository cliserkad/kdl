package com.xarql.kdl.names;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.MethodHeader;
import com.xarql.kdl.Path;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.ir.Constant;
import com.xarql.kdl.ir.Identifier;

public class Details implements ToName {

	public static final String DEFAULT_NAME = "unknown";
	public static final InternalName DEFAULT_TYPE = null;
	public static final boolean DEFAULT_MUTABLE = false;
	public static final boolean DEFAULT_NULLABLE = false;

	public static final String CHEVRON_REGEX = "[<>]";

	public final Identifier name;
	public final InternalName type;
	public final boolean mutable;

	public final boolean constant;

	public Details(final Identifier name, final InternalName type, final boolean mutable) {
		this.name = name;
		this.type = type;
		this.mutable = mutable;
		constant = false;
	}

	public Details(final Details details) {
		this(details.name, details.type, details.mutable);
	}

	public Details(final String name, final InternalName type, final boolean mutable) {
		this(new Identifier(name), type, mutable);
	}

	public Details(final String name, final InternalName type) {
		this(name, type, DEFAULT_MUTABLE);
	}

	public Details(final String name) {
		this(name, DEFAULT_TYPE);
	}

	public Details() {
		this(DEFAULT_NAME);
	}

	public Details(final kdl.DetailsContext ctx, final CompilationUnit unit) throws Exception {
		this.constant = ctx.CONST() != null;
		if(constant) {
			// type is unknown, as it is determined by the assignment
			this.type = null;
			// always immutable
			this.mutable = false;
		}
		else {
			// determine the type
			InternalName type;
			if (ctx.type().basetype() != null) {
				if (ctx.type().basetype().BOOLEAN() != null)
					type = InternalName.BOOLEAN;
				else if (ctx.type().basetype().BYTE() != null)
					type = InternalName.BYTE;
				else if (ctx.type().basetype().SHORT() != null)
					type = InternalName.SHORT;
				else if (ctx.type().basetype().CHAR() != null)
					type = InternalName.CHAR;
				else if (ctx.type().basetype().INT() != null)
					type = InternalName.INT;
				else if (ctx.type().basetype().FLOAT() != null)
					type = InternalName.FLOAT;
				else if (ctx.type().basetype().LONG() != null)
					type = InternalName.LONG;
				else if (ctx.type().basetype().DOUBLE() != null)
					type = InternalName.DOUBLE;
				else if (ctx.type().basetype().STRING() != null)
					type = InternalName.STRING;
				else
					throw new UnimplementedException(CommonText.SWITCH_BASETYPE);
			} else {
				type = unit.resolveImport(ctx.type().getText()).toInternalName();
				if (type == null)
					throw new IllegalArgumentException("Couldn't recognize type");
			}
			// detect if the type is an array
			if (ctx.type().BRACE_L() != null)
				type = type.toArray(ctx.type().BRACE_L().size());
			this.type = type;
			// detect if the value is mutable
			this.mutable = ctx.TILDE() != null;
		}

		this.name = new Identifier(ctx.IDENTIFIER().getText());
	}

	public Details withName(final String name) {
		return new Details(name, type, mutable);
	}

	public Details withType(final InternalName type) {
		return new Details(name, type, mutable);
	}

	/**
	 * Transforms init() and prep() to their respective special names
	 */
	public Details filterName() {
		if(name.text.equals(MethodHeader.S_INIT.replaceAll(CHEVRON_REGEX, CommonText.EMPTY_STRING)))
			return withName(MethodHeader.S_INIT);
		else if(name.text.equals(MethodHeader.S_STATIC_INIT.replaceAll(CHEVRON_REGEX, CommonText.EMPTY_STRING)))
			return withName(MethodHeader.S_STATIC_INIT);
		else
			return this;
	}

	/**
	 * Forwarding method
	 *
	 * @see InternalName#isBaseType()
	 */
	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	/**
	 * Forwarding method
	 *
	 * @see InternalName#toBaseType()
	 */
	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}

	/**
	 * @return type
	 */
	@Override
	public InternalName toInternalName() {
		return type;
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Details) {
			final Details other = (Details) object;
			return name.equals(other.name) && type.equals(other.type) && mutable == other.mutable;
		} else
			return false;
	}

	@Override
	public String toString() {
		final String name;
		if(this.name != null)
			name = this.name.text;
		else
			name = "???";

		final String type;
		if(this.type != null)
			type = this.type.arrayName();
		else
			type = "null";

		final String mutable;
		if(this.mutable)
			mutable = "~";
		else
			mutable = "";

		return type + mutable + " " + name;
	}

}
