package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;

public class Literal<Type> extends BasePushable implements CommonText {

	public static final Literal<Boolean> DEFAULT_BOOLEAN = new Literal<>(false);
	public static final Literal<Byte> DEFAULT_BYTE = new Literal<>((byte) 0);
	public static final Literal<Short> DEFAULT_SHORT = new Literal<>((short) 0);
	public static final Literal<Character> DEFAULT_CHAR = new Literal<>(' ');
	public static final Literal<Integer> DEFAULT_INT = new Literal<>(0);
	public static final Literal<Float> DEFAULT_FLOAT = new Literal<>(0.0F);
	public static final Literal<Long> DEFAULT_LONG = new Literal<>(0L);
	public static final Literal<Double> DEFAULT_DOUBLE = new Literal<>(0.0D);
	public static final Literal<String> DEFAULT_STRING = new Literal<>("");

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
			throw new IllegalArgumentException("Literal may only have Types defined in the BaseType enum, but the type was " + value.getClass().getName());
		else
			this.value = value;
	}

	public static Literal<?> getDefaultForBaseType(BaseType baseType) {
		return switch(baseType) {
			case BOOLEAN -> DEFAULT_BOOLEAN;
			case BYTE -> DEFAULT_BYTE;
			case SHORT -> DEFAULT_SHORT;
			case CHAR -> DEFAULT_CHAR;
			case INT -> DEFAULT_INT;
			case FLOAT -> DEFAULT_FLOAT;
			case LONG -> DEFAULT_LONG;
			case DOUBLE -> DEFAULT_DOUBLE;
			case STRING -> DEFAULT_STRING;
		};
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
		} else if(ctx.decimalNumber() != null) {
			final double val = Double.parseDouble(removeSpacers(ctx.decimalNumber().getText()));
			if(val == 0.0d || val < Float.MAX_VALUE && val > Float.MIN_VALUE)
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
						if(actor.unit.hasConstant(target))
							out.add(actor.unit.getConstant(target));
						else if(actor.unit.getCurrentScope().contains(target))
							out.add(actor.unit.getLocalVariable(target));
						else if(actor.unit.fields().contains(new ObjectField(target, null, false, actor.unit.getClazz())))
							out.add(actor.unit.fields().equivalentKey(new ObjectField(target, null, false, actor.unit.getClazz())));
						else
							throw new IllegalArgumentException(target + " was not a valid mixin target. Use " + QUOTE + ESCAPE + MIXIN + QUOTE + " for the literal text " + QUOTE + MIXIN + QUOTE);
						i += target.length();
					} else
						prev += MIXIN;
				} else if(found.charAt(i) == ESCAPE) {
					// if double escape occurs, add 1 escape to the literal and skip over the 2nd escape
					if(found.length() > i + 1 && found.charAt(i + 1) == ESCAPE) {
						prev += ESCAPE;
						i++;
					}
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
