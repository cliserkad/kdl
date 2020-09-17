package com.xarql.kdl.ir;

import java.util.ArrayList;
import java.util.List;

import com.xarql.kdl.Actor;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;

public class Literal<Type> extends BasePushable implements CommonText {

	public Type value;

	public Literal(Type value) {
		if(!BaseType.isBaseType(value))
			throw new IllegalArgumentException(
					BaseType.matchClass(value.getClass()) + " Literal may only have Types defined in the BaseType enum, but the type was " + value.getClass().getName());
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
	public String toString() {
		return "Literal: " + toBaseType().name() + " --> " + value;
	}

	@Override
	public InternalName toInternalName() {
		return toBaseType().toInternalName();
	}

	@Override
	public Pushable push(Actor visitor) {
		visitor.visitLdcInsn(value);
		return this;
	}

	public static Pushable parseLiteral(final kdl.LiteralContext ctx, Actor actor) throws Exception {
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
		} else if(ctx.decimalNumber() != null) {
			final double val = Double.parseDouble(ctx.decimalNumber().getText());
			if(val < Float.MAX_VALUE && val > Float.MIN_VALUE)
				return new Literal<>((float) val);
			else
				return new Literal<>(val);
		} else if(ctx.STRING_LIT() != null) {
			String found = crush(ctx.STRING_LIT().getText());
			StringTemplate out = new StringTemplate();
			for(String s : fragment(found)) {
				if(s.startsWith("$")) {
					if(actor.unit.hasConstant(s.substring(1)))
						out.add(actor.unit.getConstant(s.substring(1)));
					else if(actor.unit.getCurrentScope().contains(s.substring(1)))
						out.add(actor.unit.getLocalVariable(s.substring(1)));
					else
						out.add(s);
				} else
					out.add(s);
			}
			if(out.isTextOnly())
				return new Literal<>(out.toString());
			else
				return out;
		} else
			throw new UnimplementedException(SWITCH_BASETYPE);
	}

	public static List<String> fragment(String found) {
		List<String> list = new ArrayList<>();
		String part = "";
		for(int i = 0; i < found.length(); i++) {
			if(found.charAt(i) == '$' || Character.isWhitespace(found.charAt(i))) {
				list.add(part);
				part = "";
			}
			part += found.charAt(i);
		}
		list.add(part);
		return list;
	}

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}

}
