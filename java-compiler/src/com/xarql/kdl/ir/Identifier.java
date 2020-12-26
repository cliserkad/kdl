package com.xarql.kdl.ir;

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
        return text.matches(VALID_ID_REGEX);
    }

}
