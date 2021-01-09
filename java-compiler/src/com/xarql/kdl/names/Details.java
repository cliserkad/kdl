package com.xarql.kdl.names;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.MethodHeader;
import com.xarql.kdl.Type;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.ir.Identifier;

public class Details implements ToDetails {

	public static final String DEFAULT_NAME = "unknown";
	public static final TypeDescriptor DEFAULT_TYPE = null;
	public static final boolean DEFAULT_MUTABLE = false;
	public static final boolean DEFAULT_NULLABLE = false;

	public static final String CHEVRON_REGEX = "[<>]";

	public final Identifier name;
	public final TypeDescriptor descriptor;
	public final boolean mutable;

	public final boolean constant;

	public Details(final Identifier name, final TypeDescriptor descriptor, final boolean mutable) {
		this.name = name;
		this.descriptor = descriptor;
		this.mutable = mutable;
		constant = false;
	}

	public Details(final Details details) {
		this(details.name, details.descriptor, details.mutable);
	}

	public Details(final String name, final TypeDescriptor descriptor, final boolean mutable) {
		this(new Identifier(name), descriptor, mutable);
	}

	public Details(final String name, final TypeDescriptor descriptor) {
		this(name, descriptor, DEFAULT_MUTABLE);
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
			this.descriptor = null;
			// always immutable
			this.mutable = false;
		}
		else {
			// determine the type
			TypeDescriptor type;
			if (ctx.type().basetype() != null) {
				if (ctx.type().basetype().BOOLEAN() != null)
					type = BaseType.BOOLEAN.toTypeDescriptor();
				else if (ctx.type().basetype().BYTE() != null)
					type = BaseType.BYTE.toTypeDescriptor();
				else if (ctx.type().basetype().SHORT() != null)
					type = BaseType.SHORT.toTypeDescriptor();
				else if (ctx.type().basetype().CHAR() != null)
					type = BaseType.CHAR.toTypeDescriptor();
				else if (ctx.type().basetype().INT() != null)
					type = BaseType.INT.toTypeDescriptor();
				else if (ctx.type().basetype().FLOAT() != null)
					type = BaseType.FLOAT.toTypeDescriptor();
				else if (ctx.type().basetype().LONG() != null)
					type = BaseType.LONG.toTypeDescriptor();
				else if (ctx.type().basetype().DOUBLE() != null)
					type = BaseType.DOUBLE.toTypeDescriptor();
				else if (ctx.type().basetype().STRING() != null)
					type = BaseType.STRING.toTypeDescriptor();
				else
					throw new UnimplementedException(CommonText.SWITCH_BASETYPE);
			} else {
				type = unit.resolveImport(ctx.type().getText()).toTypeDescriptor();
				if (type == null)
					throw new IllegalArgumentException("Couldn't recognize type");
			}
			// detect if the type is an array
			if (ctx.type().BRACE_L() != null)
				type = type.toArray(ctx.type().BRACE_L().size());
			this.descriptor = type;
			// detect if the value is mutable
			this.mutable = ctx.TILDE() != null;
		}

		this.name = new Identifier(ctx.IDENTIFIER().getText());
	}

	public Details withName(final String name) {
		return new Details(name, descriptor, mutable);
	}

	public Details withType(final TypeDescriptor type) {
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
	 * @see TypeDescriptor#isBaseType()
	 */
	@Override
	public boolean isBaseType() {
		return descriptor.isBaseType();
	}

	/**
	 * Forwarding method
	 *
	 * @see TypeDescriptor#toBaseType()
	 */
	@Override
	public BaseType toBaseType() {
		return descriptor.toBaseType();
	}

	/**
	 * @return type
	 */
	@Override
	public Type toType() {
		return descriptor.type;
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Details) {
			final Details other = (Details) object;
			return name.equals(other.name) && descriptor.equals(other.descriptor) && mutable == other.mutable;
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
		if(this.descriptor != null)
			type = this.descriptor.arrayName();
		else
			type = "null";

		final String mutable;
		if(this.mutable)
			mutable = "~";
		else
			mutable = "";

		return type + mutable + " " + name;
	}

	@Override
	public TypeDescriptor toTypeDescriptor() {
		return descriptor;
	}

	@Override
	public Details toDetails() {
		return this;
	}
}
