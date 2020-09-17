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

	public static final char MIXIN = '$';
	public static final char QUOTE = '\"';
	public static final char ESCAPE = '\\';

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
			String prev = "";
			for(int i = 0; i < found.length(); i++) {
				if(found.charAt(i) == MIXIN) {
					if(i == 0 || found.charAt(i - 1) != ESCAPE) {
						out.add(prev);
						prev = "";
						final String target = resolveMixin(found, i);
						if(actor.unit.hasConstant(target))
							out.add(actor.unit.getConstant(target));
						else if(actor.unit.getCurrentScope().contains(target))
							out.add(actor.unit.getLocalVariable(target));
						else
							throw new IllegalArgumentException(
									target + " was not a valid mixin target. Use " + QUOTE + ESCAPE + MIXIN + QUOTE + " for the literal text " + QUOTE + MIXIN + QUOTE);
						i += target.length();
					} else
						prev += MIXIN;
				} else if(found.charAt(i) == ESCAPE) {

				} else
					prev += found.charAt(i);
			}
			if(!prev.isEmpty())
				out.add(prev);
			if(out.isTextOnly())
				return new Literal<>(out.toString());
			else
				return out;
		} else
			throw new UnimplementedException(SWITCH_BASETYPE);
	}

	public static String resolveMixin(final String s, final int start) {
		String sub = s.substring(start + 1);
		for(int i = 0; i < sub.length(); i++)
			if(!isAlphabetic(sub.charAt(i)))
				return sub.substring(0, i);
		return sub;
	}

	public static boolean isAlphabetic(final char c) {
		return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
	}

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}

}
