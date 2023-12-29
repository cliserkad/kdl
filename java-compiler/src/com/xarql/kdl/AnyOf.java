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
	public abstract <R> R match(Function<? super A, R> funcA, Function<? super B, R> funcB, Function<? super C, R> funcC);

	/**
	 * This method is protected to force the caller to access the value through
	 * a subclass to guarantee a valid type.
	 */
	protected abstract Object getValue();

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

		@Override
		public <R> R match(Function<? super A, R> funcA, Function<? super B, R> funcB, Function<? super C, R> funcC) {
			return funcA.apply(value);
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

		@Override
		public <R> R match(Function<? super A, R> funcA, Function<? super B, R> funcB, Function<? super C, R> funcC) {
			return funcB.apply(value);
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

		@Override
		public <R> R match(Function<? super A, R> funcA, Function<? super B, R> funcB, Function<? super C, R> funcC) {
			return funcC.apply(value);
		}

	}

}
