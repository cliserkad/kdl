package com.xarql.kdl;

public class StringValue extends Constant.Value {
	private final String value;

	public StringValue(String value) {
		this.value = value;
	}

	@Override
	public Class<?> valueType() {
		return String.class;
	}

	@Override
	public Object value() {
		return value;
	}

}
