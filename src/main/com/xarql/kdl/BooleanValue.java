package main.com.xarql.kdl;

public class BooleanValue extends Constant.Value {
    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public Class<?> valueType() {
        return Boolean.class;
    }

    @Override
    public Object value() {
        return value;
    }

}
