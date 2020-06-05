package com.xarql.kdl.names;

public class NameFormats {
	public static final String INIT   = "<init>";
	public static final String CLINIT = "<clinit>";

	/**
	 * Provides the internal name of the given class with L prepended and ;
	 * appended
	 * @param c Class of the object
	 * @return L + internal name + ;
	 */
	public static String internalObjectName(Class<?> c) {
		return new InternalObjectName(c).toString();
	}

	public static String internalName(Class<?> c) {
		return new InternalName(c).toString();
	}
}
