package com.xarql.kdl;

import java.util.Objects;

/**
 * ElapseTimer is a simple class that provides a way to measure the elapsed time between two points in a program. It is not a timer in the sense that it does not provide a way to schedule a task to be executed at a later time.
 */
public class ElapseTimer {

	public static final long NANO_PER_MILLI = 1000000;
	public static final long NANO_PER_SEC = NANO_PER_MILLI * 1000;

	final public long start = System.nanoTime();

	/**
	 * Provides the typically desired output, which is the difference in nanoseconds between when this ElapseTimer was instantiated and now; the elapsed time.
	 *
	 * @return elapsed time
	 */
	public long out() {
		return System.nanoTime() - start;
	}

	/**
	 * Provides the elapsed time in a human-readable format.
	 */
	@Override
	public String toString() {
		final long out = out();
		if(out / NANO_PER_SEC > 1)
			return (out / NANO_PER_SEC) + " seconds";
		else if(out / NANO_PER_MILLI > 1)
			return (out / NANO_PER_MILLI) + " milliseconds";
		else
			return out + " nanoseconds";
	}

	@Override
	public boolean equals(Object object) {
		if(this == object)
			return true;
		if(object instanceof ElapseTimer) {
			ElapseTimer that = (ElapseTimer) object;
			return start == that.start;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(start);
	}

}
