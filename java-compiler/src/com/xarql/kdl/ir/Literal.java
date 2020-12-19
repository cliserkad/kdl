package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.MethodHeader;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;

public class Literal<Type> implements Pushable, CommonText {

	public static final char MIXIN = '$';
	public static final char QUOTE = '\"';
	public static final char ESCAPE = '\\';
	public static final String SPACERS_REGEX = "[,_]";

	public Type value;

	public static void main(String[] args) {
		System.out.println(removeSpacers("1,00_0.0000_0"));   
	}

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
			final long val = Long.parseLong(removeSpacers(ctx.integer().getText()));
			if(val < Byte.MAX_VALUE && val > Byte.MIN_VALUE)
				return new Literal<>((byte) val);
			else if(val < Short.MAX_VALUE && val > Short.MIN_VALUE)
				return new Literal<>((short) val);
			else if(val < Integer.MAX_VALUE && val > Integer.MIN_VALUE)
				return new Literal<>((int) val);
			else
				return new Literal<>(val);
		} else if(ctx.fraction() != null) {
			final double val = Double.parseDouble(removeSpacers(ctx.fraction().getText()));
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
						if(!prev.isEmpty())
							out.add(prev);
						prev = "";
						final String target = resolveMixin(found, i);
						Member m = resolveVar(new Identifier(target), actor);
						out.add(m);
						i += target.length();
					} else
						prev += MIXIN;
				} else if(found.charAt(i) == ESCAPE) {
					// for escape sequences
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

	public static Member resolveVar(Identifier target, Actor actor) {
		actor.unit.getType().members().get(target);
		for(Member m : actor.unit.getType().members()) {
			if(m.details().name.equals(target) && !(m instanceof MethodHeader))
				return m;
		}
		throw new IllegalArgumentException(target + " was not a valid mixin target. Use " + QUOTE + ESCAPE + MIXIN + QUOTE + " for the literal text " + QUOTE + MIXIN + QUOTE);
	}

	public static String removeSpacers(final String s) {
		return s.replaceAll(SPACERS_REGEX, CommonText.EMPTY_STRING).trim();
	}

	public static String resolveMixin(final String s, final int start) {
		String sub = s.substring(start + 1);
		for(int i = 0; i < sub.length(); i++)
			if(!isAlphanumeric(sub.charAt(i)))
				return sub.substring(0, i);
		return sub;
	}

	public static boolean isAlphanumeric(final char c) {
		return (c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c >= 48 && c <= 57);
	}

	public static String crush(final String s) {
		return s.substring(1, s.length() - 1);
	}

}
