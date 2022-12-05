package com.xarql.kdl;

public class SymbolResolutionException extends Exception {

	// increment when this file is modified
	public static final long serialVersionUID = 1;

	public static final String PREPEND = "Fail to resolve symbol ";

	public SymbolResolutionException(final String symbolName) {
		super(PREPEND + symbolName);
	}

}
