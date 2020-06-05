package main.com.xarql.kdl.names;

import main.com.xarql.kdl.StringOutput;

public enum BaseType implements StringOutput {
	INT('I'), BOOLEAN('Z'), LONG('J'), FLOAT('F'), DOUBLE('D');

	char rep;

	BaseType(char rep) {
		this.rep = rep;
	}

	@Override
	public String stringOutput() {
		return "" + rep;
	}

	@Override
	public String toString() {
		return stringOutput();
	}
}
