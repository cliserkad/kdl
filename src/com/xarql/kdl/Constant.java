package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;
import com.xarql.kdl.names.ToBaseType;

import static com.xarql.kdl.BestList.list;

public class Constant<Type> implements ToBaseType {
	public static final BestList<Class<?>> ACCEPTABLE_TYPES = list(String.class, Integer.class, Boolean.class);

	public final String name;
	public final Type   value;

	private boolean evaluated;

	public Constant(final String name, final Type value) {
		if(name == null || name.isEmpty())
			throw new IllegalArgumentException("Constant name may not be empty");
		this.name = name;
		this.value = value;
	}

	public Constant(final String name, final Constant<Type> source) {
		this(name, source.value);
	}

	public static <Any> Any checkValueType(Any value) {
		if(!ACCEPTABLE_TYPES.contains(value.getClass()))
			throw new IllegalStateException("Constant may not have the Type of " + value.getClass() + ". Acceptable types are " + ACCEPTABLE_TYPES);
		return value;
	}

	public InternalName internalName() {
		return InternalName.internalName(value.getClass());
	}

	public InternalObjectName internalObjectName() {
		return internalName().object();
	}

	@Override
	public boolean isBaseType() {
		return BaseType.isBaseType(value);
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.matchValue(value);
	}

	@Override
	public String toString() {
		return "Constant: " + name + " --> " + value;
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

	/**
	 * Changes the evaluated field of this constant to true
	 * @return true if a change was made
	 */
	public boolean markAsEvaluated() {
		final boolean out = !evaluated;
		evaluated = true;
		return out;
	}

	/**
	 * Provides read access to the evaluated field
	 * @return evaluated
	 */
	public boolean isEvaluated() {
		return evaluated;
	}
}
