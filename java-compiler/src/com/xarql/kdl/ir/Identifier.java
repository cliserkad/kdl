package com.xarql.kdl.ir;

import com.xarql.kdl.MethodHeader;

import java.lang.reflect.Method;

public class Identifier {

	public static final String VALID_ID_REGEX = "[^\\r\\t\\n &|+<>=?!*.~:;,(){}'\"%]+";

	public final String text;
	public final IdentifierStyle style;

	public static void main(String[] args) {
		System.out.println(verify("helloWorld"));
		System.out.println(verify("hello world"));
		System.out.println(verify("hello&world"));
	}

	public Identifier(String text) {
		if(!verify(text))
			throw new IllegalArgumentException("The id " + text + " is invalid");
		this.text = text;
		this.style = IdentifierStyle.match(text);
		if(style == IdentifierStyle.IMPROPER)
			System.out.println("Warning: ID " + text + " is improper");
	}

	public static boolean verify(String text) {
		if(text.equals(MethodHeader.S_INIT) || text.equals(MethodHeader.S_STATIC_INIT))
			return true;
		else
			return text.matches(VALID_ID_REGEX);
	}

	/**
	 * Determines equality, based on the id's text. Accepts either a String or an
	 * Identifier
	 */
	public boolean equals(Object obj) {
		if(obj instanceof String) {
			return text.equals(obj);
		} else if(obj instanceof Identifier) {
			Identifier other = (Identifier) obj;
			return other.text.equals(text);
		} else
			return false;
	}

	public String toString() {
		return text;
	}

}
