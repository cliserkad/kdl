package com.xarql.kdl;

import java.util.ArrayList;

/**
 * Improved version of ArrayList that implements XML
 *
 * @param <E>
 * @author Bryan Johnson
 */
public class BestList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	@SafeVarargs
	public BestList(E... elements) {
		super();
		add(elements);
	}

	public BestList(Iterable<? extends E> elements) {
		super();
		add(elements);
	}

	@SafeVarargs
	public static <E> BestList<E> list(E... element) {
		return new BestList<>(element);
	}

	// unchecked because parent add() function in ArrayList is also unchecked
	@SafeVarargs
	public final boolean add(E... elements) {
		boolean output = true;
		for(E e : elements)
			if(!add(e))
				output = false;
		return output;
	}

	public final boolean add(Iterable<? extends E> input) {
		boolean output = true;
		for(E e : input)
			if(!add(e))
				output = false;
		return output;
	}

	public final void removeAmount(int amount) {
		for(int i = 0; i < amount; i++)
			remove(0);
	}

	public final E last() {
		return get(size() - 1);
	}

	/**
	 * Creates a string that holds the toString() of all of this list's elements
	 *
	 * @return combined toString()
	 */
	public String spread() {
		String out = "";
		for(E elm : this)
			out += elm.toString().replace("\n", "") + "\n";
		return out;
	}

	/**
	 * Concatenates toString() of every element in to one string without any
	 * separation
	 *
	 * @return concat of toString() on every element
	 */
	public String squish() {
		String out = "";
		for(E elm : this)
			out += elm.toString();
		return out;
	}

	@Override
	public String toString() {
		String out = "";
		for(E elm : this)
			out += elm.toString().replace(",", "") + ",";
		return out;
	}

}
