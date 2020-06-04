package main.com.xarql.kdl;

public class Constant {
    public final String name;
    public Value        value;

    public Constant(String name) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Constant name may not be empty");
        this.name = name;
    }

    public static abstract class Value {
        public abstract Class<?> valueType();

        public abstract Object value();
    }
}
