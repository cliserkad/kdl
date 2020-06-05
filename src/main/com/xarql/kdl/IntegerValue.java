package main.com.xarql.kdl;

public class IntegerValue extends Constant.Value {
	private final int val;

	public IntegerValue(final int val) {
		this.val = val;
	}

	@Override
	public Class<?> valueType( ) {
		return Integer.class;
	}

	@Override
	public Object value( ) {
		return val;
	}
}
