package com.xarql.kdl;

import java.util.function.Function;

/**
 * A class that can be one of three types.
 * This is useful for three unrelated types
 * that need to be stored in the same variable.
 * This class will refuse to store null values.
 */
public abstract class AnyOf<A, B, C> {

	/**
	 * prevent outside subclassing by making default constructor private
	 * refuse to store null values
	 */
	private AnyOf(Object obj) throws NullPointerException {
		failIfNull(obj);
	}

	/**
	 * used by callers to apply 1 of 3 functions to the value, depending on its type
	 */
	public <R> R match(Function<A, R> funcA, Function<B, R> funcB, Function<C, R> funcC) {
		return switch(this) {
			case ElementA<A, ?, ?> a -> funcA.apply(a.getValue());
			case ElementB<?, B, ?> b -> funcB.apply(b.getValue());
			case ElementC<?, ?, C> c -> funcC.apply(c.getValue());
			default -> throw new IllegalStateException("Failed to match type " + this.getClass().getName());
		};
	}

	/**
	 * This method is protected to force the caller to access the value through
	 * a subclass to guarantee a valid type.
	 */
	protected abstract Object getValue();

	/**
	 * Provides access to equals() method of underlying value.
	 * If the Object given is an AnyOf, it will be unwrapped to compare underlying values.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AnyOf<?, ?, ?> wrapper)
			return getValue().equals(wrapper.getValue());
		else
			return getValue().equals(obj);
	}

	/**
	 * Provides access to hashCode() method of underlying value.
	 */
	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	/**
	 * Provides access to toString() method of underlying value.
	 */
	@Override
	public String toString() {
		return getValue().toString();
	}
	
	private static void failIfNull(Object o) throws IllegalArgumentException {
		if(o == null)
			throw new IllegalArgumentException("value cannot be null");
	}

	public static final class ElementA<A, B, C> extends AnyOf<A, B, C> {

		private final A value;

		public ElementA(A value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public A getValue() {
			return value;
		}

	}

	public static final class ElementB<A, B, C> extends AnyOf<A, B, C> {

		private final B value;

		public ElementB(B value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public B getValue() {
			return value;
		}

	}

	public static final class ElementC<A, B, C> extends AnyOf<A, B, C> {

		private final C value;

		public ElementC(C value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public C getValue() {
			return value;
		}

	}

}
