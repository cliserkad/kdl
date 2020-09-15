package com.xarql.kdl;

public class SyntaxException extends Exception {

	// increment when this file is modified
	public static final long serialVersionUID = 1;

	public final String msg;
	public final int line;
	public final int column;

	public SyntaxException(String msg, int line, int column) {
		this.msg = msg;
		this.line = line;
		this.column = column;
	}

	@Override
	public String getMessage() {
		return msg + " on line " + line + " at column " + column;
	}

}
