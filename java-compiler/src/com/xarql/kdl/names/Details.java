package com.xarql.kdl.names;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;

public class Details implements ToName {

	public final String name;
	public final InternalName type;
	public final boolean mutable;

	public Details(final Details details) {
		this.name = details.name;
		this.type = details.type;
		this.mutable = details.mutable;
	}

	public Details(final String name, final InternalName type, final boolean mutable) {
		this.name = name;
		this.type = type;
		this.mutable = mutable;
	}

	public Details(final kdl.DetailsContext ctx, final CompilationUnit unit) throws Exception {
		String name = ctx.VARNAME().getText();

		InternalName type;
		if(ctx.type().basetype() != null) {
			if(ctx.type().basetype().BOOLEAN() != null)
				type = InternalName.BOOLEAN;
			else if(ctx.type().basetype().BYTE() != null)
				type = InternalName.BYTE;
			else if(ctx.type().basetype().SHORT() != null)
				type = InternalName.SHORT;
			else if(ctx.type().basetype().CHAR() != null)
				type = InternalName.CHAR;
			else if(ctx.type().basetype().INT() != null)
				type = InternalName.INT;
			else if(ctx.type().basetype().FLOAT() != null)
				type = InternalName.FLOAT;
			else if(ctx.type().basetype().LONG() != null)
				type = InternalName.LONG;
			else if(ctx.type().basetype().DOUBLE() != null)
				type = InternalName.DOUBLE;
			else if(ctx.type().basetype().STRING() != null)
				type = InternalName.STRING;
			else
				throw new UnimplementedException(CommonText.SWITCH_BASETYPE);
		} else {
			type = unit.resolveAgainstImports(ctx.type().getText());
			if(type == null)
				throw new IllegalArgumentException("Couldn't recognize type");
		}

		if(ctx.type().BRACE_OPEN() != null) {
			int dimensions = 0;
			for(int i = 0; i < ctx.type().BRACE_OPEN().size(); i++)
				dimensions++;
			type = type.toArray(dimensions);
		}

		this.name = name;
		this.type = type;
		this.mutable = ctx.MUTABLE() != null;
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

	/** @return type */
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

}
