package com.xarql.kdl.ir;

import com.xarql.kdl.StringOutput;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

public class Literal<Type> extends DefaultPushable implements StringOutput, CommonText {
	public Type value;

	public Literal(Type value) {
		if(!BaseType.isBaseType(value))
			throw new IllegalArgumentException("Literal may only have Types defined in the BaseType enum, but the type was " + value.getClass());
		else
			this.value = value;
	}

	@Override
	public boolean isBaseType() {
		return true;
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.matchValue(value);
	}

	@Override
	public String stringOutput() {
		switch(toBaseType()) {
			case INT:
			case BOOLEAN:
				return value + "";
			case STRING:
				return (String) value;
			default:
				new UnimplementedException(SWITCH_BASETYPE).printStackTrace();
				return null;
		}
	}

	@Override
	public String toString() {
		return "Literal: " + toBaseType().name() + " --> " + value;
	}

	@Override
	public InternalName toInternalName() {
		return toBaseType().toInternalName();
	}

	@Override
	public Pushable push(MethodVisitor visitor) {
		visitor.visitLdcInsn(value);
		return this;
	}

	public static Literal<?> parseLiteral(final kdl.LiteralContext ctx) throws Exception {
		if(ctx.bool() != null)
			return new Literal<>(ctx.bool().TRUE() != null);
		else if(ctx.CHAR_LIT() != null)
			return new Literal<>(ctx.CHAR_LIT().getText().charAt(1));
		else if(ctx.integer() != null) {
			final long val = Long.parseLong(ctx.integer().getText());
			if(val < Byte.MAX_VALUE && val > Byte.MIN_VALUE)
				return new Literal<>((byte) val);
			else if(val < Short.MAX_VALUE && val > Short.MIN_VALUE)
				return new Literal<>((short) val);
			else if(val < Integer.MAX_VALUE && val > Integer.MIN_VALUE)
				return new Literal<>((int) val);
			else
				return new Literal<>(val);
		}
		else if(ctx.decimalNumber() != null) {
			final double val = Double.parseDouble(ctx.decimalNumber().getText());
			if(val < Float.MAX_VALUE && val > Float.MIN_VALUE)
				return new Literal<>((float) val);
			else
				return new Literal<>(val);
		}
		else if(ctx.STRING_LIT() != null)
			return new Literal<>(crush(ctx.STRING_LIT().getText()));
		else
			throw new UnimplementedException(SWITCH_BASETYPE);
	}

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}
}
