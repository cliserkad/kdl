package com.xarql.kdl.ir;

public class Identifier {
    public final String text;

    public static void main(String[] args) {
        System.out.println(verify("helloWorld"));
        System.out.println(verify("hello world"));
        System.out.println(verify("hello&world"));
    }

    public Identifier(String text) {
        if(!verify(text))
            throw new IllegalArgumentException("The id " + text + " is invalid");
        this.text = text;
    }

    public static boolean verify(String text) {
        return text.matches("[^\\r\\t\\n &|+<>=?!*.~:;,(){}'\"%]+");
    }


}
