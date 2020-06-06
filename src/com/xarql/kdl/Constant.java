package com.xarql.kdl;

import static com.xarql.kdl.BestList.list;

public class Constant<Type> {
	public static final BestList<Class<?>> ACCEPTABLE_TYPES = list(String.class, Integer.class, Boolean.class);

	public final String name;
	public final Type   value;

	public Constant(final String name, final Type value) {
		if(name == null || name.isEmpty())
			throw new IllegalArgumentException("Constant name may not be empty");
		this.name = name;
		this.value = checkValueType(value);
	}

	public Constant(final String name, Constant<Type> source) {
		this(name, source.value);
	}

	public static <Any> Any checkValueType(Any value) {
		if(!ACCEPTABLE_TYPES.contains(value.getClass()))
			throw new IllegalStateException("Constant may not have the Type of " + value.getClass() + ". Acceptable types are " + ACCEPTABLE_TYPES);
		return value;
	}

	@Override
	public String toString() {
		return name + " : " + value;
	}

	public boolean isEmpty() {
		return value == null;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Constant) {
			Constant other = (Constant) o;
			return other.name.equals(name);
		}
		return false;
	}
}
