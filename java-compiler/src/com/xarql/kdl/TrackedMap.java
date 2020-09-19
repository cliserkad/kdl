package com.xarql.kdl;

import java.util.*;

/**
 * A container that has both a list and a map to use them as one. This was
 * primarily made for caching.
 *
 * @author Bryan Johnson
 * @param <K> Key
 * @param <E> Element
 */
public class TrackedMap<K, E> implements Iterable<E> {

	private static final Random RANDOM = new Random();

	/**
	 * The tracker retains the relationship between keys and indices.
	 */
	private final List<K> tracker;
	/**
	 * The container retains the relationship between keys and elements. Elements
	 * are always retrieved using a key
	 */
	private final HashMap<K, E> container;

	/**
	 * Creates a TrackedHashMap() and sets the tracker's capacity to 0.
	 */
	public TrackedMap() {
		tracker = new ArrayList<>();
		container = new HashMap<>();
	}

	// Stats
	/**
	 * Returns the size of this TrackedHashMap. The size is gotten from the tracker.
	 * Specifically, <code>tracker.size()</code>
	 *
	 * @return size
	 */
	public int size() {
		return tracker.size();
	}

	/**
	 * Specifies if this TrackedHashMap contains the given key. The key's existence
	 * is determined in the container. Specifically,
	 * <code>container.containsKey(key)</code>
	 *
	 * @param key The key whose existence is in question
	 * @return Existence of the key
	 */
	public boolean contains(K key) {
		return tracker.contains(key);
	}

	// Key
	/**
	 * Grabs the key at the given index from the tracker. Specifically,
	 * <code>tracker.get(index)</code>
	 *
	 * @param index The place from which a key will be grabbed
	 * @return The key at the given index
	 */
	public K key(int index) {
		return tracker.get(index);
	}

	/**
	 * Reverse the map to retrieve a key from an element.
	 *
	 * @param element matchable element
	 * @return associated key or null
	 */
	public K key(E element) {
		for(int i = 0; i < size(); i++)
			if(get(i).equals(element))
				return key(i);
		return null;
	}

	/**
	 * Grabs a key from a random index in the tracker. Specifically,
	 * <code>key(r.nextInt(size()))</code>
	 *
	 * @return A random key
	 */
	public K randomKey() {
		return key(RANDOM.nextInt(size()));
	}

	/**
	 * Grabs the last key that was added to this TrackedHashMap. The key is grabbed
	 * from the tracker. Specifically, <code>key(size() - 1)</code>
	 *
	 * @return The last added key
	 */
	public K lastKey() {
		return key(size() - 1);
	}

	/**
	 * Grabs the first key that was added to this TrackedHashMap. The key is grabbed
	 * from the tracker. Specifically, <code>key(0)</code>
	 *
	 * @return The first key
	 */
	public K firstKey() {
		return key(0);
	}

	/**
	 * Attempts to determine the index of the given key in the tracker. If this
	 * TrackedMap doesn't contain the key, then -1 is returned.
	 *
	 * @param key The key whose index is desired
	 * @return The index of the key. Or -1 if the key doesn't exist.
	 */
	public int indexOf(K key) {
		if(contains(key))
			return tracker.lastIndexOf(key);
		else
			return -1;
	}

	public List<K> keys() {
		final List<K> copy = new ArrayList<>();
		for(int i = 0; i < size(); i++)
			copy.add(key(i));
		return copy;
	}

	public K equivalentKey(K key) {
		return key(indexOf(key));
	}

	// Element
	/**
	 * Retrieves the element that is mapped to the given key.
	 *
	 * @param key The known value used to retrieve the unknown object
	 * @return The element that is being retrieved
	 */
	public E get(K key) {
		return container.get(key);
	}

	/**
	 * Retrieves the element at the given index. The element is retrieved by first
	 * getting the key at the given index from the tracker, then using that key to
	 * retrieve the element from the container.
	 *
	 * @param i Index to retrieve an element from
	 * @return The element at the given index
	 */
	public E get(int i) {
		return container.get(key(i));
	}

	/**
	 * Retrieves a random element. First gets a random key from the tracker. Then
	 * uses that key to retrieve the associated element.
	 *
	 * @return A random element.
	 */
	public E getRandom() {
		return container.get(randomKey());
	}

	// Adding
	/**
	 * Adds the key to the tracker. Adds the key and the element to the container.
	 *
	 * @param key     A key
	 * @param element An element
	 */
	public void add(K key, E element) {
		if(!contains(key)) {
			tracker.add(key);
			container.put(key, element);
		}
	}

	/**
	 * Same as add but replaces the key and element if they are already present.
	 *
	 * @param key     A key
	 * @param element An element
	 */
	public void put(K key, E element) {
		if(contains(key))
			remove(key);
		tracker.add(key);
		container.put(key, element);
	}

	// Removing
	/**
	 * Removes the key and associated element from this TrackedHashMap.
	 *
	 * @param key The key to initiate removal with
	 */
	public void remove(K key) {
		// implementation uses ==, so we need to find the equivalent object's reference
		container.remove(equivalentKey(key));
		tracker.remove(key);
	}

	/**
	 * Removes the last added key and associated element. Specifically,
	 * <code>remove(lastKey());</code>
	 */
	public void removeLast() {
		remove(lastKey());
	}

	/**
	 * Removes the first added key and associated element. Specifically,
	 * <code>remove(firstKey());</code>
	 */
	public void removeFirst() {
		remove(firstKey());
	}

	/**
	 * Removes the key and associated element at the given index. Specifically,
	 * <code>remove(key(index));</code>
	 *
	 * @param index The place at which the key and element should be removed
	 */
	public void removeAt(int index) {
		remove(key(index));
	}

	/**
	 * Empties this TrackedHashMap of all data.
	 */
	public void clear() {
		container.clear();
		tracker.clear();
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private int currentIndex = 0;

			@Override
			public boolean hasNext() {
				return currentIndex < size();
			}

			@Override
			public E next() {
				currentIndex++;
				return get(currentIndex - 1);
			}

			@Override
			public void remove() {
				removeAt(currentIndex);
			}

		};
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof TrackedMap<?, ?>) {
			TrackedMap<?, ?> map = (TrackedMap<?, ?>) o;
			return equals(map);
		} else
			return false;
	}

	/**
	 * Determines equity between this TrackedHashMap and another one with similar
	 * key and element types. Checks if each key and element are equal across maps
	 * and are in sequence.
	 *
	 * @param input A TrackedHashMap with like key and element types
	 * @return Equity of this and the input
	 */
	public boolean equals(TrackedMap<?, ?> input) {
		if(size() != input.size())
			return false;

		for(int i = 0; i < input.size(); i++) {
			if(!key(i).equals(input.key(i)))
				return false;
			if(!get(i).equals(input.get(i)))
				return false;
		}
		return true;
	}

}
